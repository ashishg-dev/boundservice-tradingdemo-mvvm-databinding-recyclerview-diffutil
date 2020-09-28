package com.devashish.machinetest.responseparser

import com.google.gson.annotations.SerializedName

data class MarketFeedResponseParser(
    @SerializedName("CacheTime") val cacheTime: Int,
    @SerializedName("Data") val data: List<DataMarketFeed>,
    @SerializedName("Message") val message: String,
    @SerializedName("Status") val status: Int
)

data class DataMarketFeed(
    @SerializedName("AvgRate") val avgRate: Double,
    @SerializedName("BidQty") val bidQty: Int,
    @SerializedName("BidRate") val bidRate: Double,
    @SerializedName("Exch") val exch: String,
    @SerializedName("ExchType") val exchType: String,
    @SerializedName("High") val high: Double,
    @SerializedName("LastQty") val lastQty: Int,
    @SerializedName("LastRate") val lastRate: Double,
    @SerializedName("Low") val low: Double,
    @SerializedName("OffQty") val offQty: Int,
    @SerializedName("OffRate") val offRate: Double,
    @SerializedName("OpenRate") val openRate: Double,
    @SerializedName("PClose") val pClose: Double,
    @SerializedName("TBidQ") val tBidQ: Int,
    @SerializedName("TOffQ") val tOffQ: Int,
    @SerializedName("TickDt") val tickDt: String,
    @SerializedName("Time") val time: Int,
    @SerializedName("Token") val token: Int,
    @SerializedName("TotalQty") val totalQty: Int
)