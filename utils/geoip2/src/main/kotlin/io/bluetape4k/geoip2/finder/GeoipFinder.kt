package io.bluetape4k.geoip2.finder

import io.bluetape4k.geoip2.Address
import java.net.InetAddress

/**
 * IP Address 정보로부터 행정 주소 [Address] 를 찾습니다.
 */
interface GeoipFinder {

    /**
     * IP Address 정보로부터 행정 주소 [Address] 를 찾습니다.
     *
     * @param ipAddress 찾을 IP Address
     * @return DB에서 찾은 주소 정보
     */
    fun findAddress(ipAddress: InetAddress): Address?
}
