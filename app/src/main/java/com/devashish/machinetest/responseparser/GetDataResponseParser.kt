package com.devashish.machinetest.responseparser

import com.google.gson.annotations.SerializedName

data class GetDataResponseParser(
    @SerializedName("watchName") val watchName: String,
    @SerializedName("message") val message: String,
    @SerializedName("status") val status: Int,
    @SerializedName("Data") val data: List<Data>
)

data class Data(
    @SerializedName("DayHigh") val dayHigh: Double,
    @SerializedName("DayLow") val dayLow: Double,
    @SerializedName("EPS") val ePS: Int,
    @SerializedName("Exch") val exch: String,
    @SerializedName("ExchType") val exchType: String,
    @SerializedName("FullName") val fullName: String,
    @SerializedName("High52Week") val high52Week: Double,
    @SerializedName("LastTradePrice") val lastTradePrice: Double,
    @SerializedName("LastTradeTime") val lastTradeTime: String,
    @SerializedName("Low52Week") val low52Week: Double,
    @SerializedName("Month") val month: Double,
    @SerializedName("Name") val name: String,
    @SerializedName("NseBseCode") val nseBseCode: Int,
    @SerializedName("PClose") val pClose: Double,
    @SerializedName("Quarter") val quarter: Double,
    @SerializedName("ScripCode") val scripCode: Int,
    @SerializedName("ShortName") val shortName: String,
    @SerializedName("Volume") val volume: Long,
    @SerializedName("Year") val year: Double
)