package io.bluetape4k.geoip2.finder

import io.bluetape4k.geoip2.Address
import io.bluetape4k.geoip2.Geoip
import io.bluetape4k.logging.KLogging
import java.net.InetAddress
import kotlin.jvm.optionals.getOrNull

class GeoipCountryFinder: GeoipFinder {

    companion object: KLogging()

    override fun findAddress(ipAddress: InetAddress): Address? {
        return Geoip.countryDatabase
            .tryCountry(ipAddress)
            .map { response -> Address.fromCountry(ipAddress, response) }
            .getOrNull()
    }
}
