package com.example.cloverwoocommerceapp

import android.os.Bundle
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
        apiClient = WooCommerceApiClient()
        searchCustomerByPhoneNumber("6477094830") // Pass the phone number as an argument
    }

    fun searchCustomerByPhoneNumber(phoneNumber: String) {
        apiClient.getCustomerStoreCreditBalance(phoneNumber, object : Callback<Customer?> {
            override fun onResponse(call: Call<Customer?>, response: Response<Customer?>) {
                val customer = response.body()
                customer?.let {
                    val storeCreditBalance = it.storeCreditBalance
                    // Display the store credit balance to the user
                    println("Store credit balance: $storeCreditBalance")
                } ?: run {
                    // Handle the null case
                    println("Customer not found or no store credit balance available")
                }
            }

            override fun onFailure(call: Call<Customer?>, t: Throwable) {
                // Handle the error case
            }
        })
    }
}