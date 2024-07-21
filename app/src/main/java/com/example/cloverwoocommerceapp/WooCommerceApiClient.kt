package com.example.cloverwoocommerceapp

import retrofit2.*
//import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class WooCommerceApiClient {
    private val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getCustomerStoreCreditBalance(phoneNumber: String?, callback: Callback<Customer>?) {
        val call = retrofit.create(WooCommerceApi::class.java).getCustomerByPhoneNumber(phoneNumber)
        call.enqueue(callback)
    }

    internal interface WooCommerceApi {
        @GET("customers")
        fun getCustomerByPhoneNumber(@Query("phone") phoneNumber: String?): Call<Customer>
    }

    companion object {
        private const val BASE_URL = "https://dicey.biz/wp-json/wc/v3/"
        private const val CONSUMER_KEY = "ck_fd49704c7f0abb0d51d8f410fc6aa5a3d0ca10e9"
        private const val CONSUMER_SECRET = "cs_c15cb676dc137fd0a2d30b8b711f7ff5107e31cb"
    }
}