package com.selfawarelab.ethan.iothomekotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.CompoundButton
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
// Probably best to fetch bridgeIp each time
class MainActivity : AppCompatActivity() {
    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Wire up UI
        connectUI()

//        turnLightOff("1")
//        turnLightOn("1")
//        turnAllLightsOn()
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

    }

    // TODO: Handle disposables
    // TODO: Leaner flow
    // TODO: Dissassemble into parameters that can be reused
    fun turnLightOff(lightName: String) {
        HueBridgeService.create(BRIDGE_FINDER_IP).getBridgeUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { response -> response[0].internalipaddress}
                .subscribe({ bridgeIpAdress ->
                    Timber.d("HueBridge: " + bridgeIpAdress)
                    val bridgeUrl = URL_PREFIX + bridgeIpAdress

                    val lightOffBody: HashMap<String, Boolean> = java.util.HashMap()
                    lightOffBody.put("on", false)

                    HueApiService.create(bridgeUrl)
                            .changeLightState(getUserName(), lightName, lightOffBody)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({hueLightChangeResponse ->
                                Timber.d("Hue light off: " + hueLightChangeResponse.success.size)
                            }, { error ->
                                Timber.e("Hue: " + error.localizedMessage)
                            })
                }, { error ->
                    Timber.e("HueBridge: " + error.localizedMessage)
                })
    }

    fun turnLightOn(lightName: String) {
        HueBridgeService.create(BRIDGE_FINDER_IP).getBridgeUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { response -> response[0].internalipaddress}
                .subscribe({ bridgeIpAdress ->
                    Timber.d("HueBridge: " + bridgeIpAdress)
                    val bridgeUrl = URL_PREFIX + bridgeIpAdress

                    val lightOnBody: HashMap<String, Boolean> = java.util.HashMap()
                    lightOnBody.put("on", true)

                    HueApiService.create(bridgeUrl)
                            .changeLightState(getUserName(), lightName, lightOnBody)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({hueLightChangeResponse ->
                                Timber.d("Hue light on: " + hueLightChangeResponse.success.size)
                            }, { error ->
                                Timber.e("Hue: " + error.localizedMessage)
                            })
                }, { error ->
                    Timber.e("HueBridge: " + error.localizedMessage)
                })
    }

    fun changeAllLights(onState: Boolean) {
        HueBridgeService.create(BRIDGE_FINDER_IP).getBridgeUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { response -> response[0].internalipaddress}
                .subscribe({ bridgeIpAdress ->
                    Timber.d("HueBridge: " + bridgeIpAdress)
                    val bridgeUrl = URL_PREFIX + bridgeIpAdress

                    val body: HashMap<String, Boolean> = java.util.HashMap()
                    body.put("on", onState)

                    HueApiService.create(bridgeUrl)
                            .changeAllLightState(getUserName(), body)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({hueLightChangeResponse ->
                                Timber.d("Hue all: " + hueLightChangeResponse.success.size)
                            }, { error ->
                                Timber.e("Hue: " + error.localizedMessage)
                            })
                }, { error ->
                    Timber.e("HueBridge: " + error.localizedMessage)
                })
    }

    fun getLights() {
        HueBridgeService.create(BRIDGE_FINDER_IP).getBridgeUrl()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { response -> response[0].internalipaddress}
                .subscribe({ bridgeIpAdress ->
                    Timber.d("HueBridge: " + bridgeIpAdress)
                    val bridgeUrl = URL_PREFIX + bridgeIpAdress

                    val lightOffBody: HashMap<String, Boolean> = java.util.HashMap()
                    lightOffBody.put("on", false)

                    HueApiService.create(bridgeUrl)
                            .getLights(getUserName())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ hueLightMap ->
                                Timber.d("Hue: " + hueLightMap.size)
                            }, { error ->
                                Timber.e("Hue: " + error.localizedMessage)
                            })
                }, { error ->
                    Timber.e("HueBridge: " + error.localizedMessage)
                })
    }
}
