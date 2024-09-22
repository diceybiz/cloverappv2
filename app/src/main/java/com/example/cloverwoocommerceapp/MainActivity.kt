package com.example.cloverwoocommerceapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.cloverwoocommerceapp.WooCommerceApiClient.Transaction

class MainActivity : AppCompatActivity() {
    private lateinit var apiClient: WooCommerceApiClient
    private lateinit var resultTextView: TextView
    private lateinit var storeCreditTenderConnector: StoreCreditTenderConnector
    private lateinit var amountEditText: EditText
    private lateinit var addButton: Button
    private lateinit var subtractButton: Button
    private lateinit var phoneNumberInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiClient = WooCommerceApiClient()
        storeCreditTenderConnector = StoreCreditTenderConnector(this)

        val searchButton: Button = findViewById(R.id.search_button)
        phoneNumberInput = findViewById(R.id.phone_number_input)

        resultTextView = findViewById(R.id.result_text_view)
        amountEditText = findViewById(R.id.amount_edit_text)
        addButton = findViewById(R.id.add_button)
        subtractButton = findViewById(R.id.subtract_button)

        searchButton.setOnClickListener {
            val phoneNumber = phoneNumberInput.text.toString()
            searchCustomerByPhoneNumber(phoneNumber)
        }

        addButton.setOnClickListener { updateStoreCredit(true) }
        subtractButton.setOnClickListener { updateStoreCredit(false) }
    }

    private fun searchCustomerByPhoneNumber(phoneNumber: String) {
        apiClient.getCustomerStoreCreditBalance(phoneNumber, object : Callback<List<Customer>> {
            override fun onResponse(
                call: Call<List<Customer>>,
                response: Response<List<Customer>>
            ) {
                val customers = response.body()
                Log.d("MainActivity", "Response code: ${response.code()}")

                if (customers != null && customers.isNotEmpty()) {
                    val customer = customers[0]
                    val storeCredit = customer.getStoreCreditBalance() ?: "0.0"
                    val fullName = "${customer.firstName.orEmpty()} ${customer.lastName.orEmpty()}"
                    resultTextView.text = "Store credit balance: $storeCredit\nFull name: $fullName"
                } else {
                    Log.d("MainActivity", "Customer not found or no store credit balance available")
                }
            }

            override fun onFailure(call: Call<List<Customer>>, t: Throwable) {
                Log.e("MainActivity", "Error fetching customer data", t)
            }
        })
    }

    private fun updateStoreCredit(isAdd: Boolean) {
        val amount = amountEditText.text.toString().toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val phoneNumber = phoneNumberInput.text.toString()
        searchCustomerByPhoneNumber(phoneNumber)

        apiClient.getCustomerStoreCreditBalance(phoneNumber, object : Callback<List<Customer>> {
            override fun onResponse(call: Call<List<Customer>>, response: Response<List<Customer>>) {
                val customers = response.body()
                if (customers != null && customers.isNotEmpty()) {
                    val customer = customers[0]
                    val email = customer.email
                    val type = if (isAdd) "credit" else "debit"
                    val order = Order() // Create an Order object
                    order.id = "order_id" // Set the order ID
                    storeCreditTenderConnector.processStoreCreditPayment(order, (amount * 100).toLong()) // Process the payment
                    val note = if (isAdd) "Store credit added" else "Store credit deducted"

                    if (email != null) {
                        apiClient.insertNewTransaction(email, amount.toString(), type, note, object : Callback<Transaction> {
                            override fun onResponse(call: Call<Transaction>, response: Response<Transaction>) {
                                if (response.isSuccessful) {
                                    apiClient.getCustomerStoreCreditBalance(phoneNumber, object : Callback<List<Customer>> {
                                        override fun onResponse(call: Call<List<Customer>>, response: Response<List<Customer>>) {
                                            val updatedCustomers = response.body()
                                            if (updatedCustomers != null && updatedCustomers.isNotEmpty()) {
                                                val updatedCustomer = updatedCustomers[0]
                                                val updatedStoreCredit = updatedCustomer.getStoreCreditBalance() ?: "0.0"
                                                resultTextView.text = "Store credit balance updated to: $updatedStoreCredit"
                                            } else {
                                                resultTextView.text = "Failed to update store credit balance"
                                            }
                                        }

                                        override fun onFailure(call: Call<List<Customer>>, t: Throwable) {
                                            resultTextView.text = "Failed to update store credit balance (Error: $t)"
                                        }
                                    })
                                } else {
                                    resultTextView.text = "Failed to update store credit balance (Error ${response.code()})"
                                }
                            }

                            override fun onFailure(call: Call<Transaction>, t: Throwable) {
                                resultTextView.text = "Failed to update store credit balance (Error: $t)"
                            }
                        })
                    } else {
                        resultTextView.text = "Customer email not found"
                    }
                } else {
                    resultTextView.text = "Customer not found or no store credit balance available"
                }
            }

            override fun onFailure(call: Call<List<Customer>>, t: Throwable) {
                resultTextView.text = "Failed to update store credit balance (Error: $t)"
            }
        })
    }

}
