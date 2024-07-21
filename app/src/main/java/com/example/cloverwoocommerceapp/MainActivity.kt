package com.example.cloverwoocommerceapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.cloverwoocommerceapp.Customer
import com.example.cloverwoocommerceapp.WooCommerceApiClient

class MainActivity : AppCompatActivity() {
    private lateinit var apiClient: WooCommerceApiClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiClient = WooCommerceApiClient()
        searchCustomerByPhoneNumber("6477094830")
    }

    fun searchCustomerByPhoneNumber(phoneNumber: String) {
        apiClient.getCustomerStoreCreditBalance(phoneNumber, object : Callback<Customer> {
            override fun onResponse(call: Call<Customer>, response: Response<Customer>) {
                val customer = response.body()
                customer?.let {
                    val storeCreditBalance = it.storeCreditBalance
                    Log.d("MainActivity", "Store credit balance: $storeCreditBalance")
                } ?: run {
                    Log.d("MainActivity", "Customer not found or no store credit balance available")
                }
            }

            override fun onFailure(call: Call<Customer>, t: Throwable) {
                Log.e("MainActivity", "Error fetching customer data", t)
            }
        })
    }
}