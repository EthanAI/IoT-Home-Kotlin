package com.selfawarelab.ethan.iothomekotlin

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface HueBridgeService {
    @GET("api/nupnp/")
    fun getBridgeIp(): Single<List<HueBridgeFinderResponse>>

    companion object {
        fun create(bridgeFinderUrl: String): HueBridgeService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(bridgeFinderUrl)
                    .build()

            return retrofit.create(HueBridgeService::class.java)
        }
    }

    data class HueBridgeFinderResponse(
            val id: String,
            val internalipaddress: String
    )
}

