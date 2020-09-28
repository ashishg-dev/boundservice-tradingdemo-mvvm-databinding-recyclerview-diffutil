package com.devashish.machinetest.model

data class MarketFeedModel(
    val responseStatus: ResponseStatus?,
    val stockDetails: ArrayList<MarketFeedDetailsModel>?
)

data class MarketFeedDetailsModel(
    val lastRate: Double,
    val token: Int
)