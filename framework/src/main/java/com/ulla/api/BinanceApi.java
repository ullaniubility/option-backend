package com.ulla.api;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;

import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author zhuyongdong
 * @Description 币安接口
 * @since 2023/5/30 9:37
 */
@RetrofitClient(baseUrl = "https://api.binance.com/")
public interface BinanceApi {

    @GET("api/v3/ticker/24hr")
    Response<Object> ticker24Hr(@Query("symbols") String symbols, @Query("type") String type);

}
