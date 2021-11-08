package com.se.wiser.adapter

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.google.chip.chiptool.R
import com.se.wiser.App
import com.se.wiser.model.*
import com.se.wiser.utils.DeviceUtil
import com.se.wiser.viewmodel.DeviceViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class BaseAdapter(private val onItemClick: ((model: Any, position: Int) -> Unit),
                           private val onOnOffClick: ((model: Any, position: Int) -> Unit)) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var viewModel: DeviceViewModel
    protected var context: Context? = null
    var dataList: MutableList<Any> = arrayListOf()
    protected var observeDeviceState: Boolean = false

    init {
        setHasStableIds(true)
        viewModel = DeviceViewModel()
    }

    fun addItem(item: Any) {
        dataList.add(item)
    }

    fun removeItem(item: Any) {
        dataList.remove(item)
    }

    fun clearItem() {
        dataList.clear()
    }

    fun updateItem(position: Int, itemData: Any, refreshAll: Boolean = true) {
        this.dataList[position] = itemData

        if (refreshAll) {
            //由于数据源只是局部变化，所以可以使用全部刷新避免局部刷新带来的闪烁问题
            this.notifyDataSetChanged()
        } else {
            this.notifyItemChanged(position)
        }
    }

    inner class DeviceHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItem(model: BaseDevice) {
            itemView.findViewById<TextView>(R.id.tvDeviceName).text = DeviceUtil.getDeviceName(model.productModeId, context!!)
            itemView.findViewById<ImageView>(R.id.ivDeviceType).setImageResource(
                DeviceUtil.getDeviceResId(
                model.productModeId
            ))
            itemView.findViewById<TextView>(R.id.tvDeviceDescription).text = "ep ${model.endpoint}"
            val stateIconImageView = itemView.findViewById<ImageView>(R.id.ivState)
            val stateTextView = itemView.findViewById<TextView>(R.id.tvDeviceState)
            val twoStateLayout = itemView.findViewById<RelativeLayout>(R.id.rlTwoElements)
            val oneElements = twoStateLayout.findViewById<TextView>(R.id.tvElement1)
            val twoElements = twoStateLayout.findViewById<TextView>(R.id.tvElement2)
            val threeElements = twoStateLayout.findViewById<TextView>(R.id.tvElement3)
            val statusIconImageView = twoStateLayout.findViewById<ImageView>(R.id.ivStatus)

            fun showOneStateIcon() {
                stateIconImageView.visibility = View.VISIBLE
                stateTextView.visibility = View.GONE
                twoStateLayout.visibility = View.GONE
            }
            fun showOneStateText() {
                stateIconImageView.visibility = View.GONE
                stateTextView.visibility = View.VISIBLE
                twoStateLayout.visibility = View.GONE
            }
            fun showTwoStateElements() {
                stateIconImageView.visibility = View.GONE
                stateTextView.visibility = View.GONE
                twoStateLayout.visibility = View.VISIBLE
            }
            fun showThreeStateElements() {
                stateIconImageView.visibility = View.GONE
                stateTextView.visibility = View.GONE
                twoStateLayout.visibility = View.VISIBLE
                statusIconImageView.visibility = View.VISIBLE
            }
            fun showFourStateElements() {
                stateIconImageView.visibility = View.GONE
                stateTextView.visibility = View.GONE
                twoStateLayout.visibility = View.VISIBLE
                threeElements.visibility = View.VISIBLE
            }
            fun setupOneState(productModeId: String, state: Int) {
                showOneStateText()
                stateTextView.text = DeviceUtil.getStateName(productModeId, state, context!!)
                stateTextView.setTextColor(itemView.resources.getColor(if (state == 1) R.color.green else R.color.content))
            }
            Log.d("james", "model=$model")
            when(model) {
                is OnOffDevice -> {
                    showOneStateIcon()
                    stateIconImageView.setColorFilter(itemView.resources.getColor(if (model.state) R.color.green else android.R.color.darker_gray))
                    stateIconImageView.setOnClickListener {
                        Log.d("ComposeAdapter", "click OnOff")
                        model.state = !model.state
                        updateItem(layoutPosition, model, false)
                        onOnOffClick.invoke(model, layoutPosition)
                    }
                }
                is DimmerDevice -> {
                    showThreeStateElements()
                    oneElements.text = model.getLevel(context!!)
                    twoElements.visibility = View.GONE
                    statusIconImageView.setColorFilter(itemView.resources.getColor(if (model.state) R.color.green else android.R.color.darker_gray))
                    statusIconImageView.setOnClickListener {
                        Log.d("ComposeAdapter", "click OnOff")
                        model.state = !model.state
                        updateItem(layoutPosition, model, false)
                        onOnOffClick.invoke(model, layoutPosition)
                    }
                }
                is MotionDevice, is DoorDevice, is WaterLeakageDevice -> {
                    if (model is MotionDevice) setupOneState(model.productModeId, model.state)
                    if (model is DoorDevice) setupOneState(model.productModeId, model.state)
                    if (model is WaterLeakageDevice) setupOneState(model.productModeId, model.state)
                }
//                is DoorDevice -> {
//                    setupOneState(model.productModeId, model.state)
//                }
//                is WaterLeakageDevice -> {
//                    setupOneState(model.productModeId, model.state)
//                }
                is TemperatureHumidityDevice -> {
                    showTwoStateElements()
                    oneElements.text = model.getTemperature(context!!)
                    twoElements.text = model.getHumidity(context!!)
                }
                is ShutterDevice -> {
                    showTwoStateElements()
                    oneElements.text = model.getLiftPercentage(context!!)
                    twoElements.text = model.getTiltPercentage(context!!)
                }
                is ElectricalMeasurementDevice -> {
                    Log.d("ComposeAdapter", "electric device")
                    showFourStateElements()
                    oneElements.text = model.getTotalActivePower(context!!)
//                    twoElements.text = model.getCurrentMin(context!!)
                    twoElements.visibility = View.GONE
                    threeElements.text = model.getVoltageMin(context!!)
                }
                is SocketDevice -> {
                    Log.d("ComposeAdapter", "socket device")
                    showFourStateElements()
                    statusIconImageView.visibility = View.VISIBLE
                    oneElements.text = model.getActivePower(context!!)
                    twoElements.text = model.getCurrent(context!!)
                    threeElements.text = model.getVoltageMin(context!!)
                    statusIconImageView.setColorFilter(itemView.resources.getColor(if (model.state) R.color.green else android.R.color.darker_gray))
                    statusIconImageView.setOnClickListener {
                        Log.d("ComposeAdapter", "click OnOff")
                        model.state = !model.state
                        updateItem(layoutPosition, model, false)
                        onOnOffClick.invoke(model, layoutPosition)
                    }
                }

            }
            itemView.setOnClickListener { onItemClick.invoke(model, layoutPosition) }
        }
    }

    fun observeDeviceState() {
        viewModel.viewModelScope.launch {
            (context?.applicationContext as App).observeDeviceState.collect {
                Log.d("ComposeAdapter", "ComposeAdapter update device state event=$it")
                dataList.mapIndexed { index, device ->
                    when (device) {
                        is OnOffDevice -> {
                            val endpoint = DeviceUtil.getEndpoint(device.type, device.endpoint)
                            val updateDevice = (context?.applicationContext as App).deviceStateMap[endpoint]
                            Log.d("ComposeAdapter", "device= $device hashmapDevice=$updateDevice")
                            if (device.endpoint == updateDevice?.endpoint) {
                                device.state = (updateDevice as? OnOffDevice)?.state ?: device.state
                                viewModel.viewModelScope.launch(Dispatchers.Main) {
                                    updateItem(index, device, false)
                                }
                            }
                        }
                        is DimmerDevice -> {
                            val endpoint = DeviceUtil.getEndpoint(device.type, device.endpoint)
                            val updateDevice = (context?.applicationContext as App).deviceStateMap[endpoint]
                            if (device.endpoint == updateDevice?.endpoint) {
                                device.state = (updateDevice as? DimmerDevice)?.state ?: device.state
                                device.level = (updateDevice as? DimmerDevice)?.level ?: device.level
                                viewModel.viewModelScope.launch(Dispatchers.Main) {
                                    updateItem(index, device, false)
                                }
                            }
                        }
                        is MotionDevice -> {
                            val endpoint = DeviceUtil.getEndpoint(device.type, device.endpoint)
                            val updateDevice = (context?.applicationContext as App).deviceStateMap[endpoint]
                            if (device.endpoint == updateDevice?.endpoint) {
                                device.copy(
                                    (updateDevice as? MotionDevice)?.state ?: device.state
                                )
                                viewModel.viewModelScope.launch(Dispatchers.Main) {
                                    updateItem(index, device, false)
                                }
                            }
                        }
                        is ShutterDevice -> {
                            val endpoint = DeviceUtil.getEndpoint(device.type, device.endpoint)
                            val updateDevice = (context?.applicationContext as App).deviceStateMap[endpoint]
                            if (device.endpoint == updateDevice?.endpoint) {
                                device.copy(
                                    (updateDevice as? ShutterDevice)?.liftPercentage ?: device.liftPercentage,
                                    (updateDevice as? ShutterDevice)?.tiltPercentage ?: device.tiltPercentage,

                                )
                                viewModel.viewModelScope.launch(Dispatchers.Main) {
                                    updateItem(index, device, false)
                                }
                            }
                        }
                        is DoorDevice -> {
                            val endpoint = DeviceUtil.getEndpoint(device.type, device.endpoint)
                            val updateDevice = (context?.applicationContext as App).deviceStateMap[endpoint]
                            if (device.endpoint == updateDevice?.endpoint) {
                                device.copy(
                                    (updateDevice as? DoorDevice)?.state ?: device.state
                                )
                                viewModel.viewModelScope.launch(Dispatchers.Main) {
                                    updateItem(index, device, false)
                                }
                            }
                        }
                        is WaterLeakageDevice -> {
                            val endpoint = DeviceUtil.getEndpoint(device.type, device.endpoint)
                            val updateDevice = (context?.applicationContext as App).deviceStateMap[endpoint]
                            if (device.endpoint == updateDevice?.endpoint) {
                                device.copy(
                                    (updateDevice as? WaterLeakageDevice)?.state ?: device.state
                                )
                                viewModel.viewModelScope.launch(Dispatchers.Main) {
                                    updateItem(index, device, false)
                                }
                            }
                        }
                        is TemperatureHumidityDevice -> {
                            val endpoint = DeviceUtil.getEndpoint(device.type, device.endpoint)
                            val updateDevice = (context?.applicationContext as App).deviceStateMap[endpoint]
                            if (device.endpoint == updateDevice?.endpoint) {
                                device.copy(
                                    (updateDevice as? TemperatureHumidityDevice)?.humidity
                                        ?: device.humidity,
                                    (updateDevice as? TemperatureHumidityDevice)?.temperature
                                        ?: device.temperature
                                )
                                viewModel.viewModelScope.launch(Dispatchers.Main) {
                                    updateItem(index, device, false)
                                }
                            }
                        }
                        is ElectricalMeasurementDevice -> {
                            val endpoint = DeviceUtil.getEndpoint(device.type, device.endpoint)
                            val updateDevice = (context?.applicationContext as App).deviceStateMap[endpoint]
                            if (device.endpoint == updateDevice?.endpoint) {
                                device.totalActivePower = (updateDevice as? ElectricalMeasurementDevice)?.totalActivePower
                                    ?: device.totalActivePower
                                device.activePower = (updateDevice as? ElectricalMeasurementDevice)?.activePower
                                    ?: device.activePower
                                device.current = (updateDevice as? ElectricalMeasurementDevice)?.current
                                    ?: device.current
                                device.currentMin = (updateDevice as? ElectricalMeasurementDevice)?.currentMin
                                    ?: device.currentMin
                                device.currentMax = (updateDevice as? ElectricalMeasurementDevice)?.currentMax
                                    ?: device.currentMax
                                device.voltage = (updateDevice as? ElectricalMeasurementDevice)?.voltage
                                    ?: device.voltage
                                device.voltageMin = (updateDevice as? ElectricalMeasurementDevice)?.voltageMin
                                    ?: device.voltageMin
                                device.voltageMax = (updateDevice as? ElectricalMeasurementDevice)?.voltageMax
                                    ?: device.voltageMax
                                viewModel.viewModelScope.launch(Dispatchers.Main) {
                                    updateItem(index, device, false)
                                }
                            }
                        }
                        is SocketDevice -> {
                            val endpoint = DeviceUtil.getEndpoint(device.type, device.endpoint)
                            val updateDevice = (context?.applicationContext as App).deviceStateMap[endpoint]
                            if (device.endpoint == updateDevice?.endpoint) {
                                device.state = (updateDevice as? SocketDevice)?.state ?: device.state
                                device.totalActivePower = (updateDevice as? SocketDevice)?.totalActivePower
                                    ?: device.totalActivePower
                                device.activePower = (updateDevice as? SocketDevice)?.activePower
                                    ?: device.activePower
                                device.current = (updateDevice as? SocketDevice)?.current
                                    ?: device.current
                                device.currentMin = (updateDevice as? SocketDevice)?.currentMin
                                    ?: device.currentMin
                                device.currentMax = (updateDevice as? SocketDevice)?.currentMax
                                    ?: device.currentMax
                                device.voltage = (updateDevice as? SocketDevice)?.voltage
                                    ?: device.voltage
                                device.voltageMin = (updateDevice as? SocketDevice)?.voltageMin
                                    ?: device.voltageMin
                                device.voltageMax = (updateDevice as? SocketDevice)?.voltageMax
                                    ?: device.voltageMax
                                viewModel.viewModelScope.launch(Dispatchers.Main) {
                                    updateItem(index, device, false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}