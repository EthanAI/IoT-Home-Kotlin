package com.selfawarelab.ethan.iothomekotlin

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import timber.log.Timber

interface HueApiService {
    @POST("api/")
    fun requestNewUserId(@Body body: HashMap<String, String>): Single<List<Map<String, HueUserIdRequestResponse>>>

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
        val DEVICETYPE_TAG = "devicetype"
        val SUCCESS_TAG = "success"
        val DEVICE_ID_PLACEHOLDER = "F0F0"

        var userId: String? = null // "dD53QxH0dD-885MoQ7m5ucysjuXxhlSFgVtL2KBp"

        private fun create(bridgeUrl: String): HueApiService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(bridgeUrl)
                    .build()

            return retrofit.create(HueApiService::class.java)
        }

        private fun buildIdRequestBody(): HashMap<String, String> {
            val body: HashMap<String, String> = java.util.HashMap()
            body.put(DEVICETYPE_TAG, DEVICE_ID_PLACEHOLDER)
            return body
        }

        fun getRequestNewUserIdSingle(): Single<Map.Entry<String, HueUserIdRequestResponse>> {
            return HueBridgeService.bridgeUrlSingle
                    .flatMap { bridgeUrl -> create(bridgeUrl).requestNewUserId(buildIdRequestBody()) }
                    .map { mapList -> mapList[0].entries.first() }
        }

        private fun buildLightChangeBody(turnOn: Boolean): HashMap<String, Boolean> {
            val lightOffBody: HashMap<String, Boolean> = java.util.HashMap()
            lightOffBody.put(ON_TAG, turnOn)
            return lightOffBody
        }

        fun changeLight(lightName: String, turnOn: Boolean) {
            HueBridgeService.bridgeUrlSingle
                    .flatMap { bridgeUrl ->
                        create(bridgeUrl).changeLightState(userId!!, lightName, buildLightChangeBody(turnOn))
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ hueLightChangeResponseList ->
                        Timber.d("Hue light off: %s", getResponseStatus(hueLightChangeResponseList))
                    }, errorHandler)
        }

        fun changeAllLights(turnOn: Boolean) {
            val body: HashMap<String, Boolean> = java.util.HashMap()
            body.put(ON_TAG, turnOn)

            HueBridgeService.bridgeUrlSingle
                    .flatMap { bridgeUrl -> create(bridgeUrl).changeAllLightState(userId!!, body) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ hueLightChangeResponseList ->
                        Timber.d("Hue all: " + getResponseTarget(hueLightChangeResponseList) + " " + getResponseStatus(hueLightChangeResponseList))
                    }, errorHandler)
        }

        fun getLightStatusList() {
            HueBridgeService.bridgeUrlSingle
                    .flatMap { bridgeUrl -> create(bridgeUrl).getLights(userId!!) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ hueLightMap ->
                        Timber.d("Hue: %s", hueLightMap.size)
                    }, errorHandler)
        }

        private val errorHandler = { error: Throwable ->
            Timber.e("HueApi: %s", error.localizedMessage)
        }
    }
}

