package com.se.wiser.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.chip.chiptool.R
import com.se.wiser.model.BaseDevice

class DeviceAdapter(private val onItemClick: ((model: Any, position: Int) -> Unit),
                    private val onOnOffClick: ((model: Any, position: Int) -> Unit)): BaseAdapter(onItemClick, onOnOffClick) {

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

//    override fun getItemViewType(position: Int): Int {
//     return when((dataList[position] as BaseDevice).type) {
//         SwitchType -> 0
//         DimmerType -> 1
//         else -> 0
//     }
//    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        context = parent.context
        if (!observeDeviceState) {
            observeDeviceState()
            observeDeviceState = true
        }
//        return when(viewType) {
//            0 -> GroupHolder(LayoutInflater.from(parent.context).inflate(R.layout.main_listitem_group_device, parent, false))
//            1 -> DeviceHolder(LayoutInflater.from(parent.context).inflate(R.layout.main_listitem_group_light_device, parent, false))
//            else -> GroupHolder(LayoutInflater.from(parent.context).inflate(R.layout.main_listitem_group_device, parent, false))
//        }
        return DeviceHolder(LayoutInflater.from(parent.context).inflate(R.layout.main_listitem_group_light_device, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        when((dataList[position] as BaseDevice).type) {
//            GroupType -> (holder as GroupHolder).bindItem(dataList[position] as GroupDevice)
            /*DeviceType -> */(holder as DeviceHolder).bindItem(dataList[position] as BaseDevice)
//        }
    }

    override fun getItemCount(): Int = dataList.size
}