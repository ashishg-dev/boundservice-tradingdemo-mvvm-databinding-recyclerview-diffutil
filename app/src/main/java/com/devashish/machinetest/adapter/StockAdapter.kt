package com.devashish.machinetest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.devashish.machinetest.R
import com.devashish.machinetest.databinding.ItemStockDetailsDataBinding
import com.devashish.machinetest.model.StockDetailsModel

class StockAdapter(
    private val data: List<StockDetailsModel>, private val listener: OnItemClickListener
) :
    ListAdapter<StockDetailsModel, StockAdapter.ItemViewHolder>(StockDiffCallback()) {

    class ItemViewHolder(private val itemStockDetailsDataBinding: ItemStockDetailsDataBinding) :
        RecyclerView.ViewHolder(itemStockDetailsDataBinding.root) {
        fun bind(item: StockDetailsModel, listener: OnItemClickListener) {
            itemStockDetailsDataBinding.details = item
            itemStockDetailsDataBinding.listener = listener
            itemStockDetailsDataBinding.position = adapterPosition
            itemStockDetailsDataBinding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view: ItemStockDetailsDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.lsv_stock_details,
            parent, false
        )
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(data[position], listener)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class StockDiffCallback : DiffUtil.ItemCallback<StockDetailsModel>() {

        override fun areItemsTheSame(
            oldItem: StockDetailsModel, newItem: StockDetailsModel
        ): Boolean {
            return oldItem.scripCode == newItem.scripCode
        }

        override fun areContentsTheSame(
            oldItem: StockDetailsModel, newItem: StockDetailsModel
        ): Boolean {
            return oldItem.lastTradePrice == newItem.lastTradePrice
        }

    }

    interface OnItemClickListener {
        fun onItemClicked(data: StockDetailsModel, position: Int)
    }


}