package com.devashish.machinetest.repository

import com.devashish.machinetest.BuildConfig
import com.devashish.machinetest.responseparser.GetDataResponseParser
import com.devashish.machinetest.responseparser.MarketFeedResponseParser
import com.devashish.machinetest.requestparser.MarketFeedRequestParser
import com.devashish.machinetest.restapi.ApiClient
import com.devashish.machinetest.restapi.ApiInterface

class StockRepository {

    private val apiService = ApiClient.createService(ApiInterface::class.java)

    suspend fun getData(): GetDataResponseParser {
        return apiService.getData(BuildConfig.SUBSCRIPTION_KEY)
    }

    suspend fun marketFeed(marketFeedRequestParser: MarketFeedRequestParser)
            : MarketFeedResponseParser {
        return apiService.marketFeed(BuildConfig.SUBSCRIPTION_KEY, marketFeedRequestParser)
    }

}