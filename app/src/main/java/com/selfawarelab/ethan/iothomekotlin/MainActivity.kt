package com.selfawarelab.ethan.iothomekotlin

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


// TODO: UI to request user to hit bridge button when needed
// TODO: get username
// TODO: store / fetch username. Handle it existing but not working
// TODO: parse light state responses
// TODO: UI confirmed
// TODO: Hue colors
// TODO: Fix Timber logging
// TODO: Handle disposables

// Probably best to fetch bridgeIp each time
class MainActivity : AppCompatActivity() {
    val USERID_KEY = MyApplication.myApplication.getString(R.string.userIdKey)

    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Wire up UI
        connectUI()

        // Initialize values
        HueApiService.getLightStatusList()

        // Check if we have a stored hueUserId. If not, request one from the bridge
        val hueUserId = readHueUserId();
        if (hueUserId != null) {
            Timber.d("Got HueId")
        } else {
            Timber.d("No HueId")

//            HueApiService.getRequestNewUserIdSingle()
//                    .subscribe({ mapEntry ->
//                        if (mapEntry.key.equals(HueApiService.SUCCESS_TAG)) {
//                            Timber.d("Hue ID " + mapEntry.value.username)
//                            writeHueUserId(mapEntry.value.username)
//                            HueApiService.userId = mapEntry.value.username
//                        } else {
//                            Toast.makeText(this, "No userId saved. Press Hue Bridge button and try again", Toast.LENGTH_SHORT).show()
//                            Timber.e("Hue ID " + mapEntry.key)
//                        }
//                    }, errorHandler)
        }
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

    fun readHueUserId(): String? {
        val sharedPref = getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getString(USERID_KEY, null)
    }

    fun writeHueUserId(hueUserId: String) {
        val sharedPrefEditor = getPreferences(Context.MODE_PRIVATE).edit()

        sharedPrefEditor.putString(USERID_KEY, hueUserId)
        sharedPrefEditor.commit()
    }

    private val errorHandler = { error: Throwable ->
        Log.e("Error ", error.localizedMessage)
        Timber.e("MainActivity: %s", error.localizedMessage)
    }
}
