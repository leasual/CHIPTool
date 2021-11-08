package com.se.wiser.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

class CommonAdapter(private val layoutIds: Array<Int>, private val modelTypes: Array<Class<*>>,
                    val bindItem: (itemView: View, model: Any, position: Int) -> Unit,
                    private val onItemClick: ((model: Any, position: Int) -> Unit),
                    areItemsTheSame: ((Any, Any) -> Boolean) = { _, _ -> false },
                    areContentsTheSame: ((Any, Any) -> Boolean) = { _, _ -> false },
                    val getItemPosition: ((model: Any, position: Int) -> Long)? = null)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var dataList: MutableList<Any> by basicDiffUtil(
        arrayListOf(),
        areItemsTheSame = areItemsTheSame,
        areContentsTheSame = areContentsTheSame
    )

    init {
        setHasStableIds(true)
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int = dataList.size

    override fun getItemViewType(position: Int): Int {
        var layoutId = 0
        for (index in modelTypes.indices) {
            val classType = modelTypes[index].simpleName
            val dataType = dataList[position].javaClass.simpleName
            Log.d("test", "index= $index classType= $classType  dataType=$dataType position= $position classType= ${dataList[position].javaClass.simpleName}")
            if (classType == dataType) {
                layoutId = layoutIds[index]
                break
            }
        }
        require(layoutId != 0) { "it can't find this type's layout" }
        return layoutId
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        bindItem(holder.itemView, dataList[position], position)
        holder.itemView.setOnClickListener { onItemClick.invoke(dataList[position], position) }
    }

    override fun getItemId(position: Int): Long {
        return getItemPosition?.invoke(dataList[position], position) ?: position.toLong()
    }

    inner class RecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    /*--------------------Recycler View---------------------*/

    inline fun <VH : RecyclerView.ViewHolder, T> RecyclerView.Adapter<VH>.basicDiffUtil(
        initialValue: MutableList<T>,
        crossinline areItemsTheSame: (T, T) -> Boolean = { old, new -> old == new },
        crossinline areContentsTheSame: (T, T) -> Boolean = { old, new -> old == new }
    ) =
        Delegates.observable(initialValue) { _, old, new ->
            DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    areItemsTheSame(old[oldItemPosition], new[newItemPosition])

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                    areContentsTheSame(old[oldItemPosition], new[newItemPosition])

                override fun getOldListSize(): Int = old.size

                override fun getNewListSize(): Int = new.size
            }).dispatchUpdatesTo(this@basicDiffUtil)
        }

}