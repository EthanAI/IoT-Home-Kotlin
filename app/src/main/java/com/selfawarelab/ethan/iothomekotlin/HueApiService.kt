package com.selfawarelab.ethan.iothomekotlin

import android.util.Log
import com.selfawarelab.ethan.iothomekotlin.HueBridgeService.Companion.BRIDGE_FINDER_IP
import com.selfawarelab.ethan.iothomekotlin.HueBridgeService.Companion.URL_PREFIX
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
        fun create(bridgeUrl: String): HueApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(bridgeUrl)
                    .build()

            return retrofit.create(HueApiService::class.java)
        }

        fun changeLight(lightName: String, turnOn: Boolean) {
            val lightOffBody: HashMap<String, Boolean> = java.util.HashMap()
            lightOffBody.put("on", turnOn)

            bridgeUrlSingle
                    .flatMap { bridgeUrl -> HueApiService.create(bridgeUrl).changeLightState(getUserName(), lightName, lightOffBody) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ hueLightChangeResponseList ->
                        Timber.d("Hue light off: %s", getResponseStatus(hueLightChangeResponseList))
                    }, errorHandler)
        }

        fun changeAllLights(turnOn: Boolean) {
            val body: HashMap<String, Boolean> = java.util.HashMap()
            body.put("on", turnOn)

            bridgeUrlSingle
                    .flatMap { bridgeUrl -> HueApiService.create(bridgeUrl).changeAllLightState(getUserName(), body) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ hueLightChangeResponseList ->
                        Timber.d("Hue all: " + getResponseTarget(hueLightChangeResponseList) + " " + getResponseStatus(hueLightChangeResponseList))
                    }, errorHandler)
        }

        fun getLightStatusList() {
            bridgeUrlSingle
                    .flatMap { bridgeUrl -> HueApiService.create(bridgeUrl).getLights(getUserName()) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ hueLightMap ->
                        Timber.d("Hue: %s", hueLightMap.size)
                    }, errorHandler)
        }

        fun getResponseTarget(hueLightChangeResponseList: List<HueLightChangeResponse>) {
            hueLightChangeResponseList[0].success.entries.first().key
        }

        fun getResponseStatus(hueLightChangeResponseList: List<HueLightChangeResponse>) {
            hueLightChangeResponseList[0].success.entries.first().value
        }

        fun getUserName(): String {
            // https://developers.meethue.com/documentation/getting-started
            // Post {"devicetype":"my_hue_app#iphone peter"}
            // Handle response of "link button not pressed"

            /*
            {
                "success": {
                    "username": "dD53QxH0dD-885MoQ7m5ucysjuXxhlSFgVtL2KBp"
                }
            }
             */
            return "dD53QxH0dD-885MoQ7m5ucysjuXxhlSFgVtL2KBp"
        }

        val toBridgeUrl = { response: List<HueBridgeService.HueBridgeFinderResponse> ->
            URL_PREFIX + response[0].internalipaddress
        }

        val bridgeUrlSingle = HueBridgeService.create(BRIDGE_FINDER_IP).getBridgeIp()
                .map(toBridgeUrl)

        val errorHandler = { error: Throwable ->
            Log.e("Error ", error.localizedMessage)
            Timber.e("HueBridge: %s", error.localizedMessage)
        }
    }
}

