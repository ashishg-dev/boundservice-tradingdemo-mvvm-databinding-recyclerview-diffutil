package com.devashish.machinetest.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.devashish.machinetest.requestparser.MarketFeedRequestParser
import com.devashish.machinetest.viewmodel.StockViewModel


class MarketFeedService : Service() {

    private val localBinder: IBinder = MyBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return localBinder
    }

    fun getMarketFeed(viewModel: StockViewModel, marketFeedRequestParser: MarketFeedRequestParser) {
        viewModel.marketFeed(marketFeedRequestParser)
    }

    // Binder is to provider access point for retrieving a service instance
    // MyBinder help providing connection between fragment and service
    inner class MyBinder : Binder() {
        fun getService(): MarketFeedService {
            return this@MarketFeedService
        }
    }
}