package com.devashish.machinetest.restapi

import com.devashish.machinetest.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {

    companion object {

        private val interceptor = HttpLoggingInterceptor()
        private val logging = interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)

        private val httpClient = OkHttpClient.Builder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()

        private val builder = Retrofit.Builder()
            .baseUrl(BuildConfig.APP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))

        fun <S> createService(serviceClass: Class<S>): S {
            val retrofit = builder.client(httpClient).build()
            return retrofit.create(serviceClass)
        }


    }

}