package com.example.cloverwoocommerceapp;


import com.google.gson.annotations.SerializedName;

public class Customer {
    @SerializedName("store_credit_balance")
    private String storeCreditBalance;

    public String getStoreCreditBalance() {
        return storeCreditBalance;
    }
}
