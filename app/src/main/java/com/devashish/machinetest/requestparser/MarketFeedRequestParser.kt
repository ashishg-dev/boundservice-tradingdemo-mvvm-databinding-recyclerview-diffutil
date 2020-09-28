package com.devashish.machinetest.requestparser

import com.google.gson.annotations.SerializedName

data class MarketFeedRequestParser(

    @SerializedName("Count") val count: Int,
    @SerializedName("MarketFeedData") val marketFeedData: List<MarketFeedData>
)

data class MarketFeedData(

    @SerializedName("Exch") val exch: String,
    @SerializedName("ExchType") val exchType: String,
    @SerializedName("ScripCode") val scripCode: Int,
    @SerializedName("ClientLoginType") val clientLoginType: Int,
    @SerializedName("RequestType") val requestType: Int

)