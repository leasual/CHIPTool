package com.se.wiser.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import chip.setuppayload.SetupPayload
import chip.setuppayload.SetupPayloadParser
import com.google.chip.chiptool.R
import com.google.chip.chiptool.databinding.ActivityScanQrcodeZxingBinding
import com.google.chip.chiptool.setuppayloadscanner.*
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.se.wiser.model.DeviceType
import com.se.wiser.model.NoneDevice


class ScanQRCodeZxingActivity : AppCompatActivity(), DecoratedBarcodeView.TorchListener, BarcodeCallback  {
    lateinit var binding: ActivityScanQrcodeZxingBinding

    private var capture: CaptureManager? = null

    private var cameraStarted = false
    private var startToNext = false
    private val REQUEST_CODE_CAMERA_PERMISSION = 100
    private var networkType: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanQrcodeZxingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        binding.include.ivBack.setOnClickListener {
            this.finish()
        }
//        if (!hasCameraPermission()) {
//            requestCameraPermission()
//        }
        networkType = intent.getIntExtra("networkType", 0)
        binding.cameraView.setTorchListener(this)
        binding.btFlashlight.setOnClickListener { switchFlashlight() }
        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            binding.btFlashlight.visibility = View.GONE
        }
        capture = CaptureManager(this, binding.cameraView)
//        capture?.initializeFromIntent(intent, savedInstanceState)
        capture?.setShowMissingCameraPermissionDialog(true)
        CaptureManager.setCameraPermissionReqCode(REQUEST_CODE_CAMERA_PERMISSION)
//        capture?.decode()
        binding.cameraView.decodeSingle(this)

        binding.btGo.setOnClickListener {
            handleScannedManualCode(binding.etCode.text.toString())
        }

//        changeMaskColor(null)
//        changeLaserVisibility(true)
//        IntentIntegrator(this).initiateScan();
//        IntentIntegrator(this)
//            .setBeepEnabled(false)
//            .initiateScan()
    }

    private fun hasFlash(): Boolean {
        return applicationContext.packageManager
            .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)
    }
    private fun switchFlashlight() {
        if ("Turn On FlashLight".trim().contentEquals(binding.btFlashlight.text.trim(), true)) {
            binding.cameraView.setTorchOn()
        } else {
            binding.cameraView.setTorchOff()
        }
    }

    override fun onTorchOn() {
        binding.btFlashlight.text = "Turn Off FlashLight"
    }

    override fun onTorchOff() {
        binding.btFlashlight.text = "Turn On FlashLight"
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        capture?.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSION) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showCameraPermissionAlert()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun barcodeResult(result: BarcodeResult?) {
        Log.d(TAG, "scan result= ${result?.result.toString()}")
        handleScannedQrCode(result?.result.toString())
    }

    private fun handleScannedManualCode(manualCode: String) {
        Handler(Looper.getMainLooper()).post {

            lateinit var payload: SetupPayload
            try {
                payload = SetupPayloadParser().parseManualEntryCode(manualCode)
                Log.d(TAG, "barCode displayValue=${manualCode}")
            } catch (ex: SetupPayloadParser.UnrecognizedQrCodeException) {
                Log.e(TAG, "Unrecognized QR Code", ex)
                Toast.makeText(this, "Unrecognized QR Code", Toast.LENGTH_SHORT).show()

                // Restart camera view.
                if (hasCameraPermission() && !cameraStarted) {

                }
                return@post
            }
            val deviceInfo = CHIPDeviceInfo(
                payload.version,
                payload.vendorId,
                payload.productId,
                payload.discriminator,
                payload.setupPinCode,
                payload.optionalQRCodeInfo.mapValues { (_, info) ->
                    QrCodeInfo(info.tag, info.type, info.data, info.int32)
                },
                payload.discoveryCapabilities
            )
            Log.d(TAG, "setupPinCode= ${payload.setupPinCode} discriminator= ${payload.discriminator}")
            if (!startToNext) {
                val intent = Intent(this, PairingActivity::class.java)
                intent.putExtra("deviceInfo", deviceInfo)
                intent.putExtra("networkType", networkType)
                startActivity(intent)
                startToNext = true
                this.finish()
            }
        }
    }

    private fun handleScannedQrCode(barcode: String) {

        Handler(Looper.getMainLooper()).post {

            lateinit var payload: SetupPayload
            try {
                payload = SetupPayloadParser().parseQrCode(barcode)
                Log.d(TAG, "barCode displayValue=${barcode}")
            } catch (ex: SetupPayloadParser.UnrecognizedQrCodeException) {
                Log.e(TAG, "Unrecognized QR Code", ex)
                Toast.makeText(this, "Unrecognized QR Code", Toast.LENGTH_SHORT).show()

                // Restart camera view.
                if (hasCameraPermission() && !cameraStarted) {

                }
                return@post
            }
            PreferenceManager.getDefaultSharedPreferences(this)
                .edit().putString("barcode", barcode).apply()
            val deviceInfo = CHIPDeviceInfo(
                payload.version,
                payload.vendorId,
                payload.productId,
                payload.discriminator,
                payload.setupPinCode,
                payload.optionalQRCodeInfo.mapValues { (_, info) ->
                    QrCodeInfo(info.tag, info.type, info.data, info.int32)
                },
                payload.discoveryCapabilities
            )
            Log.d(TAG, "setupPinCode= ${payload.setupPinCode} discriminator= ${payload.discriminator}")
            if (!startToNext) {
                val intent = Intent(this, PairingActivity::class.java)
                intent.putExtra("deviceInfo", deviceInfo)
                intent.putExtra("networkType", networkType)
                startActivity(intent)
                startToNext = true
                this.finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        capture!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        capture!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        capture!!.onDestroy()
    }

    private fun showCameraPermissionAlert() {
        AlertDialog.Builder(this)
            .setTitle(R.string.camera_permission_missing_alert_title)
            .setMessage(R.string.camera_permission_missing_alert_subtitle)
            .setPositiveButton(R.string.camera_permission_missing_alert_try_again) { _, _ ->
                requestCameraPermission()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    private fun showCameraUnavailableAlert() {
        AlertDialog.Builder(this)
            .setTitle(R.string.camera_unavailable_alert_title)
            .setMessage(R.string.camera_unavailable_alert_subtitle)
            .setPositiveButton(R.string.camera_unavailable_alert_exit) { _, _ ->
                this.finish()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        capture?.onSaveInstanceState(outState);
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return binding.cameraView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }


    private fun hasCameraPermission(): Boolean {
        return (PackageManager.PERMISSION_GRANTED
                == ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA))
    }

    private fun requestCameraPermission() {
        val permissions = arrayOf(Manifest.permission.CAMERA)
        requestPermissions(permissions, REQUEST_CODE_CAMERA_PERMISSION)
    }

    companion object {
        private const val TAG = "ScanQRCodeActivity"
        private const val REQUEST_CODE_CAMERA_PERMISSION = 100;
    }
}