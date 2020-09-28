package com.devashish.machinetest.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ActivityViewModel : ViewModel() {

    private var view1ScripData: MutableLiveData<HashMap<String, Int>>? = null
    private var view2ScripData: MutableLiveData<HashMap<String, Int>>? = null

    fun setView1ScripData(scriptCodeData: HashMap<String, Int>) {
        if (view1ScripData == null) {
            view1ScripData = MutableLiveData()
        }
        view1ScripData!!.value = scriptCodeData
    }

    fun getView1ScripData(tabName: String): Int? {
        if (view1ScripData == null) {
            view1ScripData = MutableLiveData()
        }
        return if (view1ScripData!!.value == null) {
            null
        } else {
            view1ScripData!!.value!![tabName]
        }
    }

    fun setView2ScripData(scriptCodeData: HashMap<String, Int>) {
        if (view2ScripData == null) {
            view2ScripData = MutableLiveData()
        }
        view2ScripData!!.value = scriptCodeData
    }

    fun getView2ScripData(tabName: String): Int? {
        if (view2ScripData == null) {
            view2ScripData = MutableLiveData()
        }
        return if (view2ScripData!!.value == null) {
            null
        } else {
            view2ScripData!!.value!![tabName]
        }
    }
}