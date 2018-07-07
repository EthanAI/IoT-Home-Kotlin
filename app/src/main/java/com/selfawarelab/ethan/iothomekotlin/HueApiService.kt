package com.selfawarelab.ethan.iothomekotlin

import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.*

interface HueApiService {
    @GET("api/{userName}/lights")
    fun getLights(@Path("userName") userName: String): Single<Map<String, HueLight>>

    @PUT("api/{userName}/lights/{lightName}/state")
    fun changeLightState(@Path("userName") userName: String,
                         @Path("lightName") lightName: String,
                         @Body body: HashMap<String, Boolean>)
            : Single<List<HueLightChangeResponse>>

    @PUT("api/{userName}/groups/0/action")
    fun changeAllLightState(@Path("userName") userName: String,
                            @Body body: HashMap<String, Boolean>)
            : Single<List<HueLightChangeResponse>>

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

