package io.bluetape4k.geocode

/**
 * Geocode (위경도) 로부터 주소를 찾습니다. (보통 City 범위까지 찾을 수 있습니다.
 */
interface GeocodeAddressFinder: AsyncGeocodeAddressFinder {

    /**
     * 위경도([geocode])에 해당하는 주소를 찾습니다.
     *
     * @param geocode 위경도 정보
     * @return 주소([Address]) 정보
     */
    fun findAddress(geocode: Geocode, language: String = "ko"): Address?

}
