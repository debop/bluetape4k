package io.bluetape4k.geoip2.finder

import io.bluetape4k.geoip2.Address
import io.bluetape4k.geoip2.Geoip
import io.bluetape4k.logging.KLogging
import java.net.InetAddress
import kotlin.jvm.optionals.getOrNull

/**
 * IP Address 정보로부터 국가 단위의 행정 주소 [Address] 를 찾습니다.
 */
class GeoipCountryFinder: GeoipFinder {

    companion object: KLogging()

    override fun findAddress(ipAddress: InetAddress): Address? {
        return Geoip.countryDatabase
            .tryCountry(ipAddress)
            .map { response -> Address.fromCountry(ipAddress, response) }
            .getOrNull()
    }
}
