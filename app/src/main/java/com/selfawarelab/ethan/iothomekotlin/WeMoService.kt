package com.selfawarelab.ethan.iothomekotlin

import android.util.Log
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url
import timber.log.Timber

// TODO: Get initial state
// TODO: Get changed state status
// TODO: Update UI with changes
// TODO: Scan for devices
// TODO: Get the XML ok
// TODO: Try KSOAP for android or IceSoap
interface WeMoService {
    // Content Type and SOAPACTION are mandatory
    @Headers(
            "Content-Type: text/xml",
            "SOAPACTION: \"urn:Belkin:service:basicevent:1#SetBinaryState\"")
    @POST
    fun flipSwitch(@Url url: String, @Body body: RequestBody): Single<String>

    companion object {
        private val bathroomLightControlUrl = "http://192.168.86.22:49153/upnp/control/basicevent1"
        private val bedroomLightControlUrl = "http://192.168.86.48:49153/upnp/control/basicevent1"

        fun create(): WeMoService {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .baseUrl("http:notUsed/")
                    .build()

            return retrofit.create(WeMoService::class.java)
        }

        fun changeAllLights(lightOn: Boolean) {
            changeBathroomLight(lightOn)
            changeBedroomLight(lightOn)
        }

        fun changeBathroomLight(lightOn: Boolean) {
            changeLight(bathroomLightControlUrl, lightOn)
        }

        fun changeBedroomLight(lightOn: Boolean) {
            changeLight(bedroomLightControlUrl, lightOn)
        }

        private fun changeLight(lightUrl: String, lightOn: Boolean) {
            create().flipSwitch(lightUrl, buildRequestBody(lightOn))
                    .subscribe({ response ->
                        Log.d("Wemo ", response)
                    }, errorHandler)
        }

        private fun buildRequestBody(lightOn: Boolean): RequestBody {
            val lightState = if (lightOn) 1 else 0

            val soapBodyString = """
                <?xml version="1.0" encoding="utf-8"?>
                <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                  <s:Body>
                    <u:SetBinaryState xmlns:u="urn:Belkin:service:basicevent1:1">
                      <BinaryState>%d</BinaryState>
                    </u:SetBinaryState>
                  </s:Body>
                </s:Envelope>
                """.trimIndent()
            val requestString = String.format(soapBodyString, lightState)

            val mediaType = MediaType.parse("text/xml")
            return RequestBody.create(mediaType, requestString);
        }

        val errorHandler = { error: Throwable ->
            Log.e("Error ", error.localizedMessage)
            Timber.e("Wemo: %s", error.localizedMessage)
        }
    }


}
