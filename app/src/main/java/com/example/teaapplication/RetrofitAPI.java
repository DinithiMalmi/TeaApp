package com.example.teaapplication;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitAPI {

    @POST("/ask")
    Call<ResponseBody> getMessage(@Body MsgModal query);
}