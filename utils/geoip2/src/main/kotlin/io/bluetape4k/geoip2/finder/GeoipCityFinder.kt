package io.bluetape4k.geoip2.finder

import io.bluetape4k.geoip2.Address
import io.bluetape4k.geoip2.Geoip
import io.bluetape4k.logging.KLogging
import java.net.InetAddress
import kotlin.jvm.optionals.getOrNull

class GeoipCityFinder: GeoipFinder {

    companion object: KLogging()

    override fun findAddress(ipAddress: InetAddress): Address? {
        return Geoip.cityDatabase
            .tryCity(ipAddress)
            .map { response -> Address.fromCity(ipAddress, response) }
            .getOrNull()
    }
}
