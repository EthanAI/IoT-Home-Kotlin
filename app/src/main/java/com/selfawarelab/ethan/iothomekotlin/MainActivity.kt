package com.selfawarelab.ethan.iothomekotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

// TODO: UI to request user to hit bridge button when needed
// TODO: get username
// TODO: store / fetch username. Handle it existing but not working
// TODO: parse light state responses
// TODO: UI inputs
// TODO: UI display state
// TODO: Hue colors
// TODO: Fix Timber logging
// TODO: Handle disposables

// Probably best to fetch bridgeIp each time
class MainActivity : AppCompatActivity() {
    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Wire up UI
        connectUI()

        // Initialize values
        HueApiService.getLightStatusList()
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    fun connectUI() {
        tvHello.text = "Hi bla bla"
        allLightsToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            HueApiService.changeAllLights(isChecked)
            WeMoService.changeAllLights(isChecked)
        }
        allHueLightsToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            HueApiService.changeAllLights(isChecked);
        }
        oneLightToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            HueApiService.changeLight("1", isChecked)
        }
        bathroomLightToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            WeMoService.changeBathroomLight(isChecked)
        }
        bedroomLightToggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            WeMoService.changeBedroomLight(isChecked)
        }
    }
}
