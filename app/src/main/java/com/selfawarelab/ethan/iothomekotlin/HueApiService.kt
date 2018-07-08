package com.selfawarelab.ethan.iothomekotlin

import android.util.Log
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import timber.log.Timber
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
        val ON_TAG = "on"

        private fun create(bridgeUrl: String): HueApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(bridgeUrl)
                    .build()

            return retrofit.create(HueApiService::class.java)
        }

        private fun buildLightChangeBody(turnOn: Boolean): HashMap<String, Boolean> {
            val lightOffBody: HashMap<String, Boolean> = java.util.HashMap()
                lightOffBody.put(ON_TAG, turnOn)
            return lightOffBody
        }

        fun changeLight(lightName: String, turnOn: Boolean) {
            HueBridgeService.bridgeUrlSingle
                    .flatMap { bridgeUrl ->
                        HueApiService.create(bridgeUrl).changeLightState(HueBridgeService.getUserName(), lightName, buildLightChangeBody(turnOn)) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ hueLightChangeResponseList ->
                        Timber.d("Hue light off: %s", getResponseStatus(hueLightChangeResponseList))
                    }, errorHandler)
        }

        fun changeAllLights(turnOn: Boolean) {
            val body: HashMap<String, Boolean> = java.util.HashMap()
            body.put(ON_TAG, turnOn)

            HueBridgeService.bridgeUrlSingle
                    .flatMap { bridgeUrl -> HueApiService.create(bridgeUrl).changeAllLightState(HueBridgeService.getUserName(), body) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ hueLightChangeResponseList ->
                        Timber.d("Hue all: " + getResponseTarget(hueLightChangeResponseList) + " " + getResponseStatus(hueLightChangeResponseList))
                    }, errorHandler)
        }

        fun getLightStatusList() {
            HueBridgeService.bridgeUrlSingle
                    .flatMap { bridgeUrl -> HueApiService.create(bridgeUrl).getLights(HueBridgeService.getUserName()) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ hueLightMap ->
                        Timber.d("Hue: %s", hueLightMap.size)
                    }, errorHandler)
        }


        private val errorHandler = { error: Throwable ->
            Log.e("Error ", error.localizedMessage)
            Timber.e("HueApi: %s", error.localizedMessage)
        }
    }
}

