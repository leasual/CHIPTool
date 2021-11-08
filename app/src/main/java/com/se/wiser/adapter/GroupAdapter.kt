package com.se.wiser.adapter

import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.chip.chiptool.R
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.kyleduo.switchbutton.SwitchButton
import com.se.wiser.model.BaseDevice
import com.se.wiser.model.BridgeDevice
import com.se.wiser.model.CommissioningInfo
import com.se.wiser.model.GroupDevice

class GroupAdapter(
    val onItemClick: ((model: Any, position: Int) -> Unit),
    val onOnOffClick: ((model: Any, position: Int) -> Unit),
    val onCommissionClick: ((model: Any, check: Boolean, position: Int) -> Unit)?
) : BaseAdapter(onItemClick, onOnOffClick) {

    private var commissionCountDownTimer: CountDownTimer? = null
    private val viewHolders = hashMapOf<Int, RecyclerView.ViewHolder>()
    private var currentItemView: View? = null
    var commissioningInfo: CommissioningInfo? = null
    var commissionTimeout: Int = 0

    companion object {
        const val TAG = "GroupAdapter"
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class GroupHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(model: GroupDevice) {
            itemView.findViewById<TextView>(R.id.tvDeviceName).text = model.name
            itemView.findViewById<ImageView>(R.id.ivExpand)
                .setImageResource(if (!model.expand) R.mipmap.outline_keyboard_arrow_down_white_24 else R.mipmap.outline_keyboard_arrow_up_white_24)
            itemView.findViewById<ImageView>(R.id.ivDeviceType)
                .setImageResource(R.mipmap.outline_settings_remote_white_36)
            val subListView = itemView.findViewById<RecyclerView>(R.id.rvSubDeviceList)
            val composeAdapter = ComposeAdapter(onItemClick, onOnOffClick)
            composeAdapter.dataList = model.subDeviceList
            subListView.adapter = composeAdapter
//            subListView.setHasFixedSize(true)
            (subListView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            subListView.visibility = if (model.expand) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                model.expand = !model.expand
                updateItem(layoutPosition, model)
            }
            if (model.type == BridgeDevice) {
                itemView.findViewById<SwitchButton>(R.id.swCommission).visibility = View.VISIBLE
                itemView.findViewById<SwitchButton>(R.id.swCommission).setCheckedNoEvent(model.isOpenCommissionWindow)
                itemView.findViewById<CardView>(R.id.cvCommissionWindow)?.visibility = if (model.isOpenCommissionWindow) View.VISIBLE else View.GONE
                itemView.findViewById<SwitchButton>(R.id.swCommission)
                    .setOnCheckedChangeListener { _, isChecked ->
                        onCommissionClick?.invoke(model, isChecked, layoutPosition)
                        Log.d(TAG, "commission checked change")
                        currentItemView = itemView
                        if (isChecked) startCommissionCountDownTimer() else stopCommissionCountDownTimer()
                    }
            }
        }
    }

    fun startCommissionCountDownTimer() {
        Log.d(TAG, "start count down timer")
        commissionCountDownTimer?.cancel()
        commissionCountDownTimer = null
        currentItemView?.findViewById<CardView>(R.id.cvCommissionWindow)?.visibility = View.VISIBLE
        commissionCountDownTimer = object : CountDownTimer(commissionTimeout * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.d(TAG, "tick= ${millisUntilFinished / 1000}s")
                currentItemView?.findViewById<TextView>(R.id.tvCommissionTime)?.text = "${millisUntilFinished / 1000}s"
                currentItemView?.findViewById<TextView>(R.id.tvCommissionCode)?.text = commissioningInfo?.manualCode
                try {
                    val barcodeEncoder = BarcodeEncoder()

                    val qrcodeBitmap = barcodeEncoder.encodeBitmap(
                        commissioningInfo?.qrCode,
                        BarcodeFormat.QR_CODE,
                        100,
                        100
                    )
                    currentItemView?.findViewById<ImageView>(R.id.ivQRCode)?.setImageBitmap(qrcodeBitmap)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFinish() {
                Log.d(TAG, "tick onFinish")
                currentItemView?.findViewById<SwitchButton>(R.id.swCommission)?.setCheckedNoEvent(false)
                currentItemView?.findViewById<CardView>(R.id.cvCommissionWindow)?.visibility = View.GONE
            }
        }
        commissionCountDownTimer?.start()
    }
    fun stopCommissionCountDownTimer() {
        commissionCountDownTimer?.cancel()
        commissionCountDownTimer = null
        currentItemView?.findViewById<SwitchButton>(R.id.swCommission)?.setCheckedNoEvent(false)
        currentItemView?.findViewById<CardView>(R.id.cvCommissionWindow)?.visibility = View.GONE
        currentItemView?.findViewById<TextView>(R.id.tvCommissionTime)?.text = ""
    }

    inner class DeviceHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItem(model: BaseDevice, position: Int) {
//            itemView.findViewById<TextView>(R.id.tvDeviceName).text =
//                "${model.name} ${model.id} ${if (model.state) "On" else "Off"}"
            itemView.setOnClickListener { onItemClick.invoke(model, position) }
//            itemView.findViewById<ImageView>(R.id.ivState).setOnClickListener {
//                model.state = !model.state
//                updateItem(layoutPosition, model, false)
//                onOnOffClick.invoke(model, position)
//            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        Log.d("james", "dataList[position]=${dataList[position]} is BaseDevice=${dataList[position] is BaseDevice}")
        return when (dataList[position]) {
            is GroupDevice -> 0
            is BaseDevice -> 1
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        if (!observeDeviceState) {
            observeDeviceState()
            observeDeviceState = true
        }
        return when (viewType) {
            0 -> GroupHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.main_listitem_group_device, parent, false)
            )
            1 -> DeviceHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.main_listitem_group_light_device, parent, false)
            )
            else -> GroupHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.main_listitem_group_device, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        viewHolders[position] = holder
        when (dataList[position]) {
            is GroupDevice -> (holder as GroupHolder).bindItem(dataList[position] as GroupDevice)
            is BaseDevice -> (holder as DeviceHolder).bindItem(dataList[position] as BaseDevice, position)
        }
    }

    override fun getItemCount(): Int = dataList.size
}