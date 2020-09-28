package com.devashish.machinetest.restapi

import com.devashish.machinetest.responseparser.GetDataResponseParser
import com.devashish.machinetest.responseparser.MarketFeedResponseParser
import com.devashish.machinetest.requestparser.MarketFeedRequestParser
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface ApiInterface {

    @GET("/ChartService/GetData")
    suspend fun getData(
        @Header("Ocp-Apim-Subscription-Key") key: String
    ): GetDataResponseParser

    @POST("/ChartService/MarketFeed")
    suspend fun marketFeed(
        @Header("Ocp-Apim-Subscription-Key") key: String,
        @Body marketFeedRequestParser: MarketFeedRequestParser
    ): MarketFeedResponseParser

}