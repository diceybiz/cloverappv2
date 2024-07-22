package com.example.cloverwoocommerceapp

import retrofit2.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Body
import com.google.gson.annotations.SerializedName
import android.util.Log

class WooCommerceApiClient {
    private val retrofit: Retrofit

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

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
            .addInterceptor(logging)
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


    fun insertNewTransaction(email: String, amount: String, type: String, note: String, callback: Callback<Transaction>) {
        val transaction = Transaction(amount = amount, type = type, note = note, email = email)
        val call = retrofit.create(WooCommerceApi::class.java).insertNewTransaction(transaction)
        call.enqueue(callback)
    }

    internal interface WooCommerceApi {
        @GET("customers")
        fun getCustomerByPhoneNumber(@Query("phone") phoneNumber: String?): Call<List<Customer>>

        @POST("wallet")
        fun insertNewTransaction(@Body transaction: Transaction): Call<Transaction>
    }
    data class Transaction(
        @SerializedName("amount")
        val amount: String,

        @SerializedName("note")
        val note: String,

        @SerializedName("type")
        val type: String,

        @SerializedName("email")
        val email: String
    )


    data class UpdateCustomerRequest(
        @SerializedName("meta_data")
        val metaData: List<MetaData>
    )

    data class MetaData(
        @SerializedName("key")
        val key: String,

        @SerializedName("value")
        val value: String
    )

    companion object {
        private const val BASE_URL = "https://dicey.biz/wp-json/wc/v3/"
        private const val CONSUMER_KEY = "ck_fd49704c7f0abb0d51d8f410fc6aa5a3d0ca10e9"
        private const val CONSUMER_SECRET = "cs_c15cb676dc137fd0a2d30b8b711f7ff5107e31cb"
    }
}

data class Customer(
    val id: Int,
    @SerializedName("first_name")
    val firstName: String? = null,
    @SerializedName("last_name")
    val lastName: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("meta_data")
    val metaData: List<WooCommerceApiClient.MetaData>? = null
) {
    fun getStoreCreditBalance(): String? {
        metaData?.forEach { meta ->
            if (meta.key == "_current_woo_wallet_balance") {
                return meta.value
            }
        }
        return null
    }
}
