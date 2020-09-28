package com.devashish.machinetest.model


data class StockDataModel(
    val responseStatus: ResponseStatus?,
    val stockDetails: ArrayList<StockDetailsModel>?
)

data class StockDetailsModel(
    val exch: String,
    val exchType: String,
    val scripCode: Int,
    val fullName: String,
    val shortName: String,
    val name: String,
    val volume: Long,
    val lastTradePrice: Double,
    val lastTradeTime: String,
    var lastRate: Double
)