package com.selfawarelab.ethan.iothomekotlin

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface HueApiService {
    @GET("api/{userName}/lights")
    fun getLights(@Path("userName") userName: String): Single<Map<String, HueLight>>

    companion object {
        fun create(bridgeUrl: String): HueApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(bridgeUrl)
                    .build()

            return retrofit.create(HueApiService::class.java)
        }
    }
}

