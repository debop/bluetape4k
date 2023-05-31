package io.bluetape4k.geocode.google

import com.google.maps.GeoApiContext
import io.bluetape4k.io.utils.Resourcex
import io.bluetape4k.logging.KLogging

object GoogleGeoService: KLogging() {

    /**
     * 구글 맵 Geocode API 를 사용하기 위해서는 API Key 를 생성해야 합니다.
     * 현재 sunghyouk.bae@gmail.com 으로 계정을 만들었습니다.
     *
     * 참고: [API 사용하기](https://developers.google.com/maps/documentation/javascript/get-api-key?hl=ko)
     */
    internal val apiKey: String by lazy {
        Resourcex.getString("GoogleGeocodeApi.key")
    }

    val context: GeoApiContext by lazy {
        geoApiContext {
            apiKey(apiKey)
            maxRetries(3)
        }
    }
}
