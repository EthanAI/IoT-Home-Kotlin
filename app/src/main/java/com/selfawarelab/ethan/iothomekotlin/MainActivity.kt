package com.selfawarelab.ethan.iothomekotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

// TODO: UI to request user to hit bridge button when needed
// TODO: get username
// TODO: store / fetch username. Handle it existing but not working
// TODO: parse light state responses
// TODO: UI inputs
// TODO: UI display state
// TODO: colors
// TODO: shared code refactor
// TODO: Fix Timber logging
// Probably best to fetch bridgeIp each time
class MainActivity : AppCompatActivity() {
    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Wire up UI
        connectUI()

        // Initialize values
        getLightStatusList()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    fun connectUI() {
        tvHello.text = "Hi bla bla"
        allLightsToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            changeAllLights(isChecked);
        }
        oneLightToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            changeLight("1", isChecked)
        }
    }

    // TODO: Handle disposables
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

    val toBridgeUrl = { response: List<HueBridgeService.HueBridgeFinderResponse> ->
        URL_PREFIX + response[0].internalipaddress
    }

    val bridgeUrlSingle = HueBridgeService.create(BRIDGE_FINDER_IP).getBridgeIp()
            .subscribeOn(Schedulers.io())
            .map(toBridgeUrl)

    val errorHandler = { error: Throwable ->
        Timber.e("HueBridge: %s", error.localizedMessage)
    }
}
