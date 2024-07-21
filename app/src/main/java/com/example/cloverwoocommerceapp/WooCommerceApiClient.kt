package com.example.cloverwoocommerceapp;

public class WooCommerceApiClient {
    private static final String BASE_URL = "https://dicey.biz/wp-json/wc/v3/";
    private static final String CONSUMER_KEY = "ck_fd49704c7f0abb0d51d8f410fc6aa5a3d0ca10e9";
    private static final String CONSUMER_SECRET = "cs_c15cb676dc137fd0a2d30b8b711f7ff5107e31cb";

    private Retrofit retrofit;

    public WooCommerceApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public void getCustomerStoreCreditBalance(String phoneNumber, Callback<Customer> callback) {
        Call<Customer> call = retrofit.create(WooCommerceApi.class).getCustomerByPhoneNumber(phoneNumber);
        call.enqueue(callback);
    }

    interface WooCommerceApi {
        @GET("customers")
        Call<Customer> getCustomerByPhoneNumber(@Query("phone") String phoneNumber);
    }