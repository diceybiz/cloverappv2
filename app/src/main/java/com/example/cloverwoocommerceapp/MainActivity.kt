package com.example.cloverwoocommerceapp

import com.google.gson.Gson
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call

import retrofit2.Callback
import retrofit2.Response

import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import com.example.cloverwoocommerceapp.Customer
import com.example.cloverwoocommerceapp.WooCommerceApiClient

class MainActivity : AppCompatActivity() {
    private lateinit var apiClient: WooCommerceApiClient


    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiClient = WooCommerceApiClient()

        val searchButton: Button = findViewById(R.id.search_button)
        val phoneNumberInput: EditText = findViewById(R.id.phone_number_input)

        resultTextView = findViewById(R.id.result_text_view)

        searchButton.setOnClickListener {
            val phoneNumber = phoneNumberInput.text.toString()
            searchCustomerByPhoneNumber(phoneNumber)
        }
    }

    fun searchCustomerByPhoneNumber(phoneNumber: String) {

        apiClient.getCustomerStoreCreditBalance(phoneNumber, object : Callback<List<Customer>> {
            override fun onResponse(call: Call<List<Customer>>, response: Response<List<Customer>>) {
                val customers = response.body()
                Log.d("MainActivity", "Response code: ${response.code()}")



                // Log the raw JSON response body


                val rawJsonResponse = response.raw().body()?.string()
                Log.d("MainActivity", "Raw JSON response body: $rawJsonResponse")
                if (customers != null && customers.isNotEmpty()) {
                    val customer = customers[0]
                    val storeCreditBalance = customer.storeCreditBalance ?: "No store credit available"
                    resultTextView.text = "Store credit balance: $storeCreditBalance"
                } else {
                    Log.d("MainActivity", "Customer not found or no store credit balance available")
                }
            }
            override fun onFailure(call: Call<List<Customer>>, t: Throwable) {
                Log.e("MainActivity", "Error fetching customer data", t)
            }
        })
    }
}
