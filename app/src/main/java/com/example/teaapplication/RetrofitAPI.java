package com.example.teaapplication;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RetrofitAPI {

    @GET
    Call<ResponseBody> getMessage(@Url String url);
}