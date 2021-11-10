package com.se.wiser.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.chip.chiptool.R
import com.se.wiser.model.BaseDevice
import com.se.wiser.model.ComposeDevice

class ComposeAdapter(private val onItemClick: ((model: Any, position: Int) -> Unit),
                     private val onOnOffClick: ((model: Any, position: Int) -> Unit)): BaseAdapter(onItemClick, onOnOffClick) {

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ComposeHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bindItem(model: ComposeDevice) {
            Log.d("james", "model=$model")
            itemView.findViewById<TextView>(R.id.tvComposeDeviceName).text = model.name
            itemView.findViewById<ImageView>(R.id.ivComposeExpand).setImageResource(if (!model.expand) R.mipmap.outline_keyboard_arrow_down_white_24 else R.mipmap.outline_keyboard_arrow_up_white_24)
            itemView.findViewById<ImageView>(R.id.ivComposeDeviceType).setImageResource(R.mipmap.outline_settings_remote_white_36)
            val subListView = itemView.findViewById<RecyclerView>(R.id.rvComposeDeviceList)
            val deviceAdapter = DeviceAdapter(onItemClick, onOnOffClick)
            deviceAdapter.dataList = model.subDeviceList
            subListView.adapter = deviceAdapter
//            subListView.setHasFixedSize(true)
            (subListView.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            subListView.visibility = if (model.expand) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                model.expand = !model.expand
                updateItem(layoutPosition, model, false)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        Log.d("james", "compose dataList[position]=${dataList[position]} is basedevice=${dataList[position] is BaseDevice}")
     return when(dataList[position]) {
         is ComposeDevice -> 0
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
        return when(viewType) {
            0 -> ComposeHolder(LayoutInflater.from(parent.context).inflate(R.layout.main_listitem_compose_device, parent, false))
            1 -> DeviceHolder(LayoutInflater.from(parent.context).inflate(R.layout.main_listitem_group_light_device, parent, false))
            else -> ComposeHolder(LayoutInflater.from(parent.context).inflate(R.layout.main_listitem_compose_device, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(dataList[position]) {
            is ComposeDevice -> (holder as ComposeHolder).bindItem(dataList[position] as ComposeDevice)
            is BaseDevice -> (holder as DeviceHolder).bindItem(dataList[position] as BaseDevice)
        }
    }

    override fun getItemCount(): Int = dataList.size
}