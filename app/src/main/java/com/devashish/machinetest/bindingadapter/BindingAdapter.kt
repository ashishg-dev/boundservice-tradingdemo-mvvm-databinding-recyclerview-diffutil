package com.devashish.machinetest.bindingadapter

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["lastRate", "lastTradePrice"], requireAll = true)
fun calculateChange(view: TextView, lastRate: Double, lastTradePrice: Double) {

    if (lastRate > 0 && lastTradePrice > 0) {
        val changes = ((lastRate - lastTradePrice) / lastTradePrice) * 100
        val finalChanges = "%.2f".format(changes) // 2 decimal place
        view.text = "$finalChanges %"
    } else {
        view.text = ""
    }

}