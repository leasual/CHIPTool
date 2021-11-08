package com.se.wiser.view

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.chip.chiptool.ChipClient
import com.google.chip.chiptool.R
import com.google.chip.chiptool.databinding.ActivityMainBinding
import com.google.chip.chiptool.util.DeviceIdUtil
import com.se.wiser.App
import com.se.wiser.adapter.CommonAdapter
import com.se.wiser.adapter.GroupAdapter
import com.se.wiser.model.*
import com.se.wiser.utils.ClusterUtil
import com.se.wiser.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "MainActivity"
    }

    private val viewModel: MainViewModel by lazy { MainViewModel(application as App) }

    private lateinit var binding: ActivityMainBinding
    private var isFirstGetDevice = true
    private val groupAdapter: GroupAdapter by lazy {
        GroupAdapter({
            model, _ -> startToDevice(model)
        }, {
            model, _ ->
            when(model) {
                is OnOffDevice -> { toggle(model.type, model.endpoint, model.state) }
                is DimmerDevice -> { toggle(model.type, model.endpoint, model.state) }
                is SocketDevice -> { toggle(model.type, model.endpoint, model.state) }

            }
        }, { model, check, _ ->
            Log.d(TAG, "check commission")
            if (check) {
                val lastDeviceId = (application as App).deviceIdMap[(model as BaseDevice).type.javaClass.simpleName] ?: 0L
                viewModel.openCommissioningWindow(lastDeviceId, 3840)
            }
        })
    }

    private fun toggle(type: DeviceType, endpoint: Int, state: Boolean) {
        val lastDeviceId = (application as App).deviceIdMap[type.javaClass.simpleName] ?: 0L
        Log.d(TAG, "main lastDeviceId= $lastDeviceId")
        viewModel.viewModelScope.launch {
            ClusterUtil.sendOnOff(
                endpoint, lastDeviceId,
                state, this@MainActivity)
        }
    }

    private fun startToDevice(model: Any) {
        when (model) {
            is OnOffDevice -> {
                start(LightControlActivity::class.java, model)
            }
            is DimmerDevice -> {
                start(LightControlActivity::class.java, model)
            }
            is DoorDevice -> {
                start(DoorSensorActivity::class.java, model)
            }
            is ShutterDevice -> {
                start(WindowCoveringActivity::class.java, model)
            }
            is SocketDevice -> {
                start(SocketControlActivity::class.java, model)
            }
        }
    }

    private fun <T: AppCompatActivity, M: BaseDevice>start(clazz: Class<T>, model: M) {
        val intent = Intent(this, clazz)
        intent.putExtra("device", model)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
        viewModel.viewModelScope.launch {
            viewModel.uiState.collect { uiModel ->
                uiModel.apply {
                    deviceList?.let {
                        setupDeviceListView(it)
                    }
                    isLoading?.let { binding.pbLoading.visibility = if (it) View.VISIBLE else View.GONE }
                    commissionState?.let {
                        Log.d(TAG, "commissionState= $it")
                        when(it) {
                            true -> { }
                            else -> {
                                groupAdapter.stopCommissionCountDownTimer()
                                Toast.makeText(this@MainActivity, "open commission windown failure.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    revokeCommissionState?.let {
                        when(it) {
                            true -> groupAdapter.stopCommissionCountDownTimer()
                            else -> Toast.makeText(this@MainActivity, "revoke commission windown failure.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    commissioningInfo?.let {
                        if (it.manualCode.isNotEmpty()) {
                            groupAdapter.commissioningInfo = it
                        }
                    }
                }
            }
        }
    }



    private fun setupViews() {
        val title = PreferenceManager.getDefaultSharedPreferences(this)
            .getString("title", "Kevin's Home")
        binding.include.tvTitle.text = title
        binding.include.ivTitleAddDevice.setOnClickListener {
            startActivity(Intent(this, AddDeviceActivity::class.java))
        }
        binding.btAddDevice.setOnClickListener {
            startActivity(Intent(this, AddDeviceActivity::class.java))
        }
        binding.ivAddDevice.setOnClickListener {
            startActivity(Intent(this, AddDeviceActivity::class.java))
        }
        binding.include.rlTitle.setOnClickListener {
            showChangeTitleDialog()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permissions = arrayOf<String>(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            for (str in permissions) {
                if (checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, 111)
                    break
                }
            }
        }
    }

    private fun showChangeTitleDialog() {
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.change_title_name, null)
        builder.setView(view)
            // Add action buttons
            .setPositiveButton(null, null)
            .setNegativeButton(null, null)
        val dialog = builder.create()
        view.findViewById<MaterialButton>(R.id.btSave).setOnClickListener {
            val title = view.findViewById<EditText>(R.id.etTitle).text.toString()
            binding.include.tvTitle.text = title
            PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .putString("title", title)
                .apply()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun checkHasNewPairingDevices(): Boolean {
        return (application as App).pairGatewayState
    }

    private fun setupDeviceListView(deviceList: ArrayList<Any> = arrayListOf()) {
        groupAdapter.dataList = deviceList
        binding.rvDeviceList.adapter = groupAdapter
        groupAdapter.commissionTimeout = (application as App).commissionTimeout
    }

    override fun onResume() {
        super.onResume()
        if (checkHasNewPairingDevices()) {
            if((application as App).currentConfigType == 0 && (application as App).hasNewBridgePairingCompleted) {
                val lastDeviceId = (application as App).deviceIdMap[BridgeDevice.javaClass.simpleName] ?: 0L
                viewModel.getDeviceList(lastDeviceId, BridgeDevice)
            } else if((application as App).currentConfigType == 1 && (application as App).hasNewBridgePairingCompleted) {
                val lastDeviceId = (application as App).deviceIdMap[ThreadDevice.javaClass.simpleName] ?: 0L
                viewModel.getDeviceList(lastDeviceId, ThreadDevice)
            } else if((application as App).currentConfigType == 2 && (application as App).hasNewBridgePairingCompleted) {
                val lastDeviceId = (application as App).deviceIdMap[WIFIDevice.javaClass.simpleName] ?: 0L
                viewModel.getDeviceList(lastDeviceId, WIFIDevice)
            }
            (application as App).hasNewBridgePairingCompleted = false
        }
        binding.rvDeviceList.visibility = if ((application as App).hasAnyCommissionComplete) View.VISIBLE else View.GONE
        binding.llNoDevice.visibility = if ((application as App).hasAnyCommissionComplete) View.GONE else View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private val TIME_EXIT = 1500
    private var mBackPressed: Long = 0
    override fun onBackPressed() {
        if (mBackPressed + TIME_EXIT > System.currentTimeMillis()) {
            (application as App).clearAllCache()
            super.onBackPressed()
            return
        } else {
            Toast.makeText(this, getString(R.string.please_click_again_to_exit), Toast.LENGTH_SHORT).show()
            mBackPressed = System.currentTimeMillis()
        }
    }
}