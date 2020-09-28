package com.devashish.machinetest.viewmodel

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devashish.machinetest.Resource
import com.devashish.machinetest.model.*
import com.devashish.machinetest.repository.StockRepository
import com.devashish.machinetest.requestparser.MarketFeedRequestParser
import com.devashish.machinetest.service.MarketFeedService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class StockViewModel : ViewModel() {

    private val repository = StockRepository()
    private var getDataResult: MutableLiveData<Resource<StockDataModel>>? = null
    val marketFeedResult = MutableLiveData<Resource<MarketFeedModel>>()
    private var view1Data: MutableLiveData<StockDetailsModel>? = null
    private var view2Data: MutableLiveData<StockDetailsModel>? = null
    private var view1ScripCode: MutableLiveData<Int>? = null
    private var myBinder = MutableLiveData<MarketFeedService.MyBinder?>()

    private val serviceConnection = object : ServiceConnection {
        //onServiceConnected gets trigger when the client is bound to the service
        override fun onServiceConnected(
            className: ComponentName, service: IBinder
        ) {
            val binder = service as MarketFeedService.MyBinder
            myBinder.postValue(binder)
        }

        //onServiceDisconnected gets trigger when the client is unbound from the service
        override fun onServiceDisconnected(name: ComponentName) {
            myBinder.postValue(null)
        }
    }

    fun getServiceConnection(): ServiceConnection {
        return serviceConnection
    }

    fun getMyBinder(): MutableLiveData<MarketFeedService.MyBinder?> {
        return myBinder
    }

    fun setView1ScripCode(scriptCode: Int) {
        if (view1ScripCode == null) {
            view1ScripCode = MutableLiveData()
        }
        view1ScripCode!!.value = scriptCode
    }

    fun getView1ScripCode(): Int? {
        if (view1ScripCode == null) {
            view1ScripCode = MutableLiveData()
        }
        return view1ScripCode!!.value
    }

    fun setView1Data(data: StockDetailsModel) {
        if (view1Data == null) {
            view1Data = MutableLiveData()
        }
        view1Data!!.value = data
    }

    fun getView1Data(): MutableLiveData<StockDetailsModel> {
        if (view1Data == null) {
            view1Data = MutableLiveData()
        }
        return view1Data!!
    }

    fun getView2Data(): MutableLiveData<StockDetailsModel> {
        if (view2Data == null) {
            view2Data = MutableLiveData()
        }
        return view2Data!!
    }

    fun setView2Data(data: StockDetailsModel) {
        if (view2Data == null) {
            view2Data = MutableLiveData()
        }
        view2Data!!.value = data
    }

    fun getStockData(): MutableLiveData<Resource<StockDataModel>> {

        if (getDataResult == null) {
            getDataResult = MutableLiveData()
            getDataResult!!.postValue(Resource.loading(null))
            viewModelScope.launch(Dispatchers.IO) {
                try {

                    val dataResponseParser = repository.getData() // from api

                    if (dataResponseParser.status == 0) {
                        val stockDetails = ArrayList<StockDetailsModel>()
                        dataResponseParser.data.forEach { item ->
                            stockDetails.add(
                                StockDetailsModel(
                                    item.exch,
                                    item.exchType,
                                    item.scripCode,
                                    item.fullName,
                                    item.shortName,
                                    item.name,
                                    item.volume,
                                    item.lastTradePrice,
                                    item.lastTradeTime,
                                    item.lastTradePrice // is the lastRate
                                )
                            )
                        }

                        getDataResult!!.postValue(
                            Resource.success(StockDataModel(null, stockDetails))
                        )

                    } else {
                        getDataResult!!.postValue(
                            Resource.error(
                                StockDataModel(
                                    ResponseStatus(
                                        dataResponseParser.status, dataResponseParser.message
                                    ), null
                                )
                            )
                        )
                    }

                } catch (e: Exception) {
                    getDataResult!!.postValue(
                        Resource.error(
                            StockDataModel(
                                ResponseStatus(0, "Something went wrong"),
                                null
                            )
                        )
                    )
                }
            }
        }

        return getDataResult!!
    }

    fun marketFeed(marketFeedRequestParser: MarketFeedRequestParser):
            MutableLiveData<Resource<MarketFeedModel>> {
        marketFeedResult.postValue(Resource.loading(null))
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // get the market feed details from api
                val marketFeedResponseParser = repository.marketFeed(marketFeedRequestParser)

                if (marketFeedResponseParser.status == 0) {
                    val stockDetails = ArrayList<MarketFeedDetailsModel>()
                    marketFeedResponseParser.data.forEach { item ->
                        stockDetails.add(MarketFeedDetailsModel(item.lastRate, item.token))
                    }
                    marketFeedResult.postValue(
                        Resource.success(MarketFeedModel(null, stockDetails))
                    )
                } else {
                    marketFeedResult.postValue(
                        Resource.error(
                            MarketFeedModel(
                                ResponseStatus(
                                    marketFeedResponseParser.status,
                                    marketFeedResponseParser.message
                                ), null
                            )
                        )
                    )
                }


            } catch (e: Exception) {
                marketFeedResult.postValue(
                    Resource.error(
                        MarketFeedModel(
                            ResponseStatus(0, "Something went wrong"),
                            null
                        )
                    )
                )
            }

        }
        return marketFeedResult
    }


}