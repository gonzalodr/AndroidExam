package com.moviles.courses.common

object Constants {
    private const val IS_PROD   = false
    private const val DEV_IP    = "192.168.1.98" //ip of the computer remember to change it
    private const val PROD_IP   = "api.miapp.com"
    private const val PORT      = "5275"

    val API_BASE_URL = if (IS_PROD) {
        "https://$PROD_IP/"
    } else {
        "http://$DEV_IP:$PORT/"
    }

    val IMAGES_URL = if(IS_PROD){
        "https://$PROD_IP/uploads/"
    }else{
        "http://$DEV_IP:$PORT/uploads/"
    }
}