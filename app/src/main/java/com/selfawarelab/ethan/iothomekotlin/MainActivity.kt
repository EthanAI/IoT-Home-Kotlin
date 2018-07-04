package com.selfawarelab.ethan.iothomekotlin

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val hueApiService = HueApiService.create()

        disposable = hueApiService.getLights(getUserName())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { hueLightMap ->
                            Timber.d("Hue: " + hueLightMap.size)
                        },
                        { error ->
                            Timber.e("Hue: " + error.localizedMessage)
                        }
                )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}
