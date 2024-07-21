package com.example.cloverwoocommerceapp

import retrofit2.*
import okhttp3.OkHttpClient
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

class WooCommerceApiClient {
    private val retrofit: Retrofit

    init {


        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val originalHttpUrl = original.url

                val url = originalHttpUrl.newBuilder()
                    .addQueryParameter("consumer_key", CONSUMER_KEY)
                    .addQueryParameter("consumer_secret", CONSUMER_SECRET)
                    .build()

                val requestBuilder = original.newBuilder().url(url)
                val request = requestBuilder.build()
                chain.proceed(request)
            }

            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun getCustomerStoreCreditBalance(phoneNumber: String?, callback: Callback<List<Customer>>) {
        val call = retrofit.create(WooCommerceApi::class.java).getCustomerByPhoneNumber(phoneNumber)
        call.enqueue(callback)
    }

    internal interface WooCommerceApi {
        @GET("customers")
        fun getCustomerByPhoneNumber(@Query("phone") phoneNumber: String?): Call<List<Customer>>
    }

    companion object {
        private const val BASE_URL = "https://dicey.biz/wp-json/wc/v3/"
        private const val CONSUMER_KEY = "ck_fd49704c7f0abb0d51d8f410fc6aa5a3d0ca10e9"
        private const val CONSUMER_SECRET = "cs_c15cb676dc137fd0a2d30b8b711f7ff5107e31cb"
    }
}