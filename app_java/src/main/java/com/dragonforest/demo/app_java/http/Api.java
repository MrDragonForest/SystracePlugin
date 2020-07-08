package com.dragonforest.demo.app_java.http;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * create by DragonForest at 2020/7/7
 */
public interface Api {
    @GET("article/top/json")
     Call<String> getData();
}
