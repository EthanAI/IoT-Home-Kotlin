package com.selfawarelab.ethan.iothomekotlin

fun getHueBridgeUrl(): String {
    // TODO: Upnp work
    return "http://192.168.86.30/"
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
