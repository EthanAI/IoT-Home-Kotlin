package com.selfawarelab.ethan.iothomekotlin


data class HueBridgeFinderResponse(
        val id: String,
        val internalipaddress: String
)

data class HueUserIdRequestResponse(
        val username: String
)

data class HueLight(
        val state: HueLightState,
        val swupdate: SwUpdate,
        val type: String,
        val name: String,
        val modelid: String,
        val manufacturername: String,
        val productname: String,
        val capabilities: HueLightCapabilities,
        val config: HueConfig,
        val uniqueid: String,
        val swversion: String
)

data class HueLightState(
        val on: Boolean,
        val bri: Integer,
        val hue: Integer,
        val sat: Integer,
        val effect: String,
        val xy: List<Float>,
        val ct: Integer,
        val alert: String,
        val colormode: String,
        val mode: String,
        val reachable: Boolean
)

data class SwUpdate(
        val state: String,
        val lastinstall: String
)

data class HueLightCapabilities(
        val certified: Boolean,
        val control: HueControl,
        val streaming: HueStreaming

)

data class HueControl(
        val mindimlevel: Integer,
        val maxlumen: Integer,
        val colorgamuttype: String,
        val colorgamut: List<Float>,
        val ct: HueCt
)

data class HueCt(
        val max: Integer,
        val min: Integer
)

data class HueStreaming(
        val renderer: Boolean,
        val proxy: Boolean
)

data class HueConfig(
        val archetype: String,
        val function: String,
        val direction: String
)

// TODO: make a list wrapper
data class HueLightChangeResponse(
        val success: Map<String, Boolean>
)

fun getResponseTarget(hueLightChangeResponseList: List<HueLightChangeResponse>) {
    hueLightChangeResponseList[0].success.entries.first().key
}

fun getResponseStatus(hueLightChangeResponseList: List<HueLightChangeResponse>) {
    hueLightChangeResponseList[0].success.entries.first().value
}
