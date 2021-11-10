package com.se.wiser.utils

import android.content.Context
import android.util.Log
import com.github.druk.dnssd.*
import com.se.wiser.model.AddressResponse
import com.se.wiser.model.BrowserResponse
import com.se.wiser.model.ResolveResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.UnknownHostException

object MDNSUtil {
    private const val TAG = "MDNSUtil"
    private var dnssd: DNSSD? = null
    private var dnssdService: DNSSDService? = null

    fun stop() {
        dnssdService?.stop()
    }

    suspend fun browser(context: Context, isBridge: Boolean = true): BrowserResponse {
        if (dnssd == null) {
            dnssd = DNSSDBindable(context)
        }
        return callbackFlow {
            val callback = object : BrowseListener {
                override fun operationFailed(service: DNSSDService?, errorCode: Int) {
                    Log.e(TAG, "error: $errorCode")
                }

                override fun serviceFound(
                    browser: DNSSDService?,
                    flags: Int,
                    ifIndex: Int,
                    serviceName: String?,
                    regType: String?,
                    domain: String?
                ) {
                    Log.i(TAG, "Found serviceName=$serviceName domain=$domain browser=$browser")
                    trySend(
                        BrowserResponse(
                        flags, ifIndex, serviceName, regType, domain
                    )
                    )
                }

                override fun serviceLost(
                    browser: DNSSDService?,
                    flags: Int,
                    ifIndex: Int,
                    serviceName: String?,
                    regType: String?,
                    domain: String?
                ) {
                    Log.i(TAG, "serviceLost")
                }
            }
            try {
                val dnssd: DNSSD = DNSSDBindable(context)
                if (isBridge) {
                    dnssdService = dnssd.browse("_matterc._udp", callback)
                } else {
                    dnssdService = dnssd.browse("_matter._tcp", callback)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            awaitClose { }
        }.first()
    }

    suspend fun startResolve(flags: Int,
                                     ifIndex: Int,
                                     serviceName: String,
                                     regType: String,
                                     domain: String): ResolveResponse {
        return callbackFlow {
            try {
                dnssd?.resolve(flags, ifIndex, serviceName, regType, domain, object : ResolveListener {
                    override fun serviceResolved(
                        resolver: DNSSDService?,
                        flags: Int,
                        ifIndex: Int,
                        fullName: String?,
                        hostName: String,
                        port: Int,
                        txtRecord: Map<String, String>
                    ) {
                        Log.d(TAG, "Resolved $hostName")
                        trySend(
                            ResolveResponse(
                            ifIndex,
                            serviceName,
                            regType,
                            domain,
                            hostName,
                            port,
                            txtRecord
                        )
                        )
                    }

                    override fun operationFailed(service: DNSSDService?, errorCode: Int) {}
                })
            } catch (e: DNSSDException) {
                e.printStackTrace()
            }

            awaitClose {  }
        }.first()
    }

    suspend fun startRecord(ifIndex: Int,
                                    serviceName: String,
                                    regType: String,
                                    domain: String,
                                    hostName: String,
                                    port: Int,
                                    txtRecord: Map<String, String>): AddressResponse {
        return callbackFlow<AddressResponse> {
            val listener: QueryListener = object : QueryListener {
                override fun queryAnswered(
                    query: DNSSDService?,
                    flags: Int,
                    ifIndex: Int,
                    fullName: String,
                    rrtype: Int,
                    rrclass: Int,
                    rdata: ByteArray?,
                    ttl: Int
                ) {
                    Log.d(TAG, "Query address $fullName")
                    try {
                        val address: InetAddress = InetAddress.getByAddress(rdata)
                        if (address is Inet4Address) {
                            Log.d(TAG, "ip4= ${address as Inet4Address} port=$port")
                        } else if (address is Inet6Address) {
                            Log.d(TAG, "ip6= ${address as Inet6Address} port=$port")
                        }
                        if (port == 5540) {
                            trySend(AddressResponse(address.hostAddress, port))
                        }
                    } catch (e: UnknownHostException) {
                        e.printStackTrace()
                    }
                }

                override fun operationFailed(service: DNSSDService?, errorCode: Int) {
                    trySend(AddressResponse("", 0))
                }
            }
            dnssd?.queryRecord(0, ifIndex, hostName, 1, 1, listener)
            dnssd?.queryRecord(0, ifIndex, hostName, 28, 1, listener)
            awaitClose {  }
        }.first()
    }
}