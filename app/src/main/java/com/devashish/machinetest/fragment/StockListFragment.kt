package com.devashish.machinetest.fragment

import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.devashish.machinetest.R
import com.devashish.machinetest.adapter.StockAdapter
import com.devashish.machinetest.databinding.FragmentStockListBinding
import com.devashish.machinetest.enum.HttpStatus
import com.devashish.machinetest.model.StockDetailsModel
import com.devashish.machinetest.requestparser.MarketFeedData
import com.devashish.machinetest.requestparser.MarketFeedRequestParser
import com.devashish.machinetest.service.MarketFeedService
import com.devashish.machinetest.viewmodel.StockViewModel
import kotlinx.android.synthetic.main.fragment_stock_list.*


class StockListFragment : Fragment(), StockAdapter.OnItemClickListener {

    private val stockViewModel by lazy {
        ViewModelProvider(this).get(StockViewModel::class.java)
    }

    private val ARG_PARAM1 = "param1"
    private var tabName: String? = null
    private val DELAY = 5000L
    private lateinit var communicateWithActivityViewModel: CommunicateWithActivityViewModel
    private lateinit var fragmentStockListBinding: FragmentStockListBinding
    private var arrayListStockData = ArrayList<StockDetailsModel>()
    private lateinit var stockAdapter: StockAdapter
    private lateinit var handlerMarketFeed: Handler
    private lateinit var handlerMarketFeedScrip: Handler
    private lateinit var runnableMarketFeed: Runnable
    private lateinit var runnableMarketFeedScrip: Runnable
    private var boundService: MarketFeedService? = null
    private var isFragmentRecreated = false
    private var scripCodeView1: Int? = null
    private var scripCodeView2: Int? = null

    companion object {
        fun newInstance(name: String) = StockListFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_PARAM1, name)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        communicateWithActivityViewModel = context as CommunicateWithActivityViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { bundle ->
            tabName = bundle.getString(ARG_PARAM1)
        }
        Log.d(tabName, "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        fragmentStockListBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_stock_list, container, false
        )
        Log.d(tabName, "onCreateView")
        return fragmentStockListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        stockAdapter = StockAdapter(arrayListStockData, this)
        recyclerViewStockList.apply {
            adapter = stockAdapter
            addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        }
        observeMyBinder()
        observeStockList()
        observeMarketFeed()
        observeView1()
        observeView2()
        Log.d(tabName, "onViewCreated")
    }

    override fun onStart() {
        super.onStart()
        // checking fragment is recreated
        when {
            scripCodeView1 != null -> {
                // this is get executed when fragment onStop method is not called (TAB switch)
                // that means not configuration changes and fragment is not recreated
                isFragmentRecreated = false

            }
            stockViewModel.getView1ScripCode() != null -> {
                // configuration changed -> getting the view1 scrip code from view model and
                // binding the service for scrip which will run every 5 sec for market feed api
                isFragmentRecreated = false

            }
            else -> {
                isFragmentRecreated = true
            }
        }
        Log.d(tabName, "onStart")
    }

    // unbind all the service which will run every 5 sec for market feed api.
    override fun onResume() {
        super.onResume()

        startAndBindService()

        handlerForScrip()

        when {
            scripCodeView1 != null -> {
                // this is get executed when fragment onStop method is not called (TAB switch)
                // that means not configuration changes and fragment is not recreated
                isFragmentRecreated = false
                handlerForScrip(arrayListStockData.filter { it.scripCode == scripCodeView1 }[0])

            }
            stockViewModel.getView1ScripCode() != null -> {
                // configuration changed -> getting the view1 scrip code from view model and
                // binding the service for scrip which will run every 5 sec for market feed api
                isFragmentRecreated = false
                scripCodeView1 = stockViewModel.getView1ScripCode()
                handlerForScrip(arrayListStockData.filter { it.scripCode == scripCodeView1 }[0])

            }
            else -> {
                isFragmentRecreated = true
            }
        }

        Log.d(tabName, "onResume")
    }

    // unbind all the service
    override fun onPause() {
        super.onPause()
        removeCallbacks()
        unbindService()
        Log.d(tabName, "onPause")
    }

    override fun onStop() {
        super.onStop()

        scripCodeView1?.let { scripCode ->
            // store the view1  scrip code into view model to survive configuration changes
            stockViewModel.setView1ScripCode(scripCode)

            // store the view1 scrip code into activity view model to survive fragment recreate
            val details = HashMap<String, Int>()
            details[tabName!!] = scripCode
            communicateWithActivityViewModel.setView1ScripData(details)
        }

        scripCodeView2?.let { scripCode ->
            // store the view2 scrip code into activity view model to survive fragment recreate
            val details = HashMap<String, Int>()
            details[tabName!!] = scripCode
            communicateWithActivityViewModel.setView2ScripData(details)
        }

        Log.d(tabName, "onStop")
    }

    private fun startAndBindService() {
        val intent = Intent(activity, MarketFeedService::class.java)
        activity!!.startService(intent)
        // BIND_AUTO_CREATE -> that the service will get created if it hasn't already when this
        // activity has binds to it
        activity!!.bindService(intent, stockViewModel.getServiceConnection(), BIND_AUTO_CREATE)
    }

    private fun unbindService() {
        boundService?.let {
            activity!!.unbindService(stockViewModel.getServiceConnection())
        }
    }

    private fun observeMyBinder() {
        stockViewModel.getMyBinder().observe(this, Observer { binder ->
            binder?.let {
                boundService = it.getService()
            }
        })
    }

    private fun observeStockList() {

        stockViewModel.getStockData().observe(this, { response ->

            when (response.status) {
                HttpStatus.LOADING -> {
                    // Loading data
                }

                HttpStatus.SUCCESS -> {

                    val data = response.data!!
                    data.stockDetails?.let { details ->
                        Log.d(tabName, details[0].lastTradePrice.toString())
                        arrayListStockData.addAll(details)
                        stockAdapter.notifyDataSetChanged()
                    }

                    if (isFragmentRecreated) {
                        handleFragmentRecreated()
                    }

                }

                HttpStatus.ERROR -> {
                    Toast.makeText(
                        activity, response.data!!.responseStatus!!.message, Toast.LENGTH_SHORT
                    ).show()
                }

            }

        })

    }

    private fun observeMarketFeed() {

        stockViewModel.marketFeedResult.observe(this, { response ->

            when (response.status) {
                HttpStatus.LOADING -> {
                    // Loading data
                }

                HttpStatus.SUCCESS -> {

                    val data = response.data!!

                    data.stockDetails?.let { stockDetails ->
                        if (stockDetails.size == 1 && scripCodeView1 != null) {
                            // update ui View 1 scrip
                            val dataMarketFeed = stockDetails[0]

                            val result = arrayListStockData.filter { item ->
                                item.scripCode == dataMarketFeed.token
                            }

                            if (result.isNotEmpty()) {
                                result[0].lastRate = dataMarketFeed.lastRate
                                if (result[0].scripCode == scripCodeView1) {
                                    stockViewModel.setView1Data(result[0])
                                }
                            }

                        } else {
                            // update ui recyclerview all scrips
                            stockDetails.forEach { dataMarketFeed ->

                                val result = arrayListStockData.filter { item ->
                                    item.scripCode == dataMarketFeed.token
                                }

                                if (result.isNotEmpty()) {
                                    result[0].lastRate = dataMarketFeed.lastRate
                                    if (result[0].scripCode == scripCodeView2) {
                                        stockViewModel.setView2Data(result[0])
                                    }
                                }
                            }

                            stockAdapter.notifyDataSetChanged()
                        }
                    }

                }

                HttpStatus.ERROR -> {
                    Toast.makeText(
                        activity, response.data!!.responseStatus!!.message, Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })

    }

    // updating view1 scrip UI
    private fun observeView1() {

        stockViewModel.getView1Data().observe(this, { data ->
            fragmentStockListBinding.view1Details = data
        })

    }

    // updating view2 scrip UI
    private fun observeView2() {

        stockViewModel.getView2Data().observe(this, { data ->
            fragmentStockListBinding.view2Details = data
        })

    }

    // creating request body for every scrip
    private fun generateJsonForMarketFeed(): MarketFeedRequestParser {
        val listData = ArrayList<MarketFeedData>()
        arrayListStockData.forEach { item ->
            listData.add(
                MarketFeedData(
                    item.exch, item.exchType, item.scripCode, 0, 0
                )
            )
        }

        return MarketFeedRequestParser(1, listData)
    }

    // creating request body for single scrip based on input parameter
    private fun generateJsonForMarketFeed(item: StockDetailsModel): MarketFeedRequestParser {
        val listData = ArrayList<MarketFeedData>()
        listData.add(
            MarketFeedData(
                item.exch, item.exchType, item.scripCode, 0, 0
            )
        )
        return MarketFeedRequestParser(1, listData)

    }

    // handling item click event based on position
    override fun onItemClicked(data: StockDetailsModel, position: Int) {

        if ((position + 1) % 2 == 0) { // even position clicked
            scripCodeView2 = data.scripCode
            stockViewModel.setView2Data(data)
        } else { // odd position clicked
            scripCodeView1 = data.scripCode
            // get the latest feed of scrip from api
            stockViewModel.marketFeed(generateJsonForMarketFeed(data))
            handlerForScrip(data)
        }

    }

    // creating handler for scrip in view 1 and running of every 5 sec
    private fun handlerForScrip(item: StockDetailsModel) {
        handlerMarketFeedScrip = Handler()
        handlerMarketFeedScrip.apply {
            runnableMarketFeedScrip = object : Runnable {

                override fun run() {
                    boundService?.let {
                        it.getMarketFeed(stockViewModel, generateJsonForMarketFeed(item))
                        postDelayed(this, DELAY)
                    }
                }
            }
            postDelayed(runnableMarketFeedScrip, DELAY)
        }
    }

    // creating handler for all scrip in recyclerview and running of every 5 sec
    private fun handlerForScrip() {
        handlerMarketFeed = Handler()
        handlerMarketFeed.apply {
            runnableMarketFeed = object : Runnable {

                override fun run() {
                    boundService?.let {
                        it.getMarketFeed(stockViewModel, generateJsonForMarketFeed())
                        postDelayed(this, DELAY)
                    }
                }
            }
            postDelayed(runnableMarketFeed, DELAY)
        }
    }

    // stop the handler
    private fun removeCallbacks() {
        handlerMarketFeed.removeCallbacks(runnableMarketFeed)
        if (this::handlerMarketFeedScrip.isInitialized) {
            handlerMarketFeedScrip.removeCallbacks(runnableMarketFeedScrip)
        }
    }

    private fun handleFragmentRecreated() {
        // getting the details from activity View Model for view1
        val scrip1FromActivity = communicateWithActivityViewModel.getView1ScripData(tabName!!)
        if (scrip1FromActivity != null) {
            scripCodeView1 = scrip1FromActivity
            val view1model = arrayListStockData.filter { it.scripCode == scrip1FromActivity }
            if (view1model.isNotEmpty()) {
                stockViewModel.setView1Data(view1model[0])
                handlerForScrip(view1model[0])
            }
        }

        // getting the details from activity View Model for view2
        val scrip2FromActivity = communicateWithActivityViewModel.getView2ScripData(tabName!!)
        if (scrip2FromActivity != null) {
            scripCodeView2 = scrip2FromActivity
            val view2model = arrayListStockData.filter { it.scripCode == scrip2FromActivity }
            if (view2model.isNotEmpty()) {
                stockViewModel.setView2Data(view2model[0])
            }
        }
    }

    interface CommunicateWithActivityViewModel {
        fun setView1ScripData(scriptCodeData: HashMap<String, Int>)
        fun getView1ScripData(tabName: String): Int?
        fun setView2ScripData(scriptCodeData: HashMap<String, Int>)
        fun getView2ScripData(tabName: String): Int?
    }

}