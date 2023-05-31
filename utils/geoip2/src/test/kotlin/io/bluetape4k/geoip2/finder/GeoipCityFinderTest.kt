package io.bluetape4k.geoip2.finder

import io.bluetape4k.geoip2.AbstractGeoipTest
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.warn
import org.amshove.kluent.shouldBeNull
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.net.InetAddress

class GeoipCityFinderTest: AbstractGeoipTest() {

    companion object: KLogging()

    val cityFinder = GeoipCityFinder()

    @ParameterizedTest(name = "find city for {0}")
    @MethodSource("getIpAddresses")
    fun `find city by ip address`(host: String) {
        val ipAddress = InetAddress.getByName(host)
        val address = cityFinder.findAddress(ipAddress)

        log.debug { "find city=$address" }
        if (address == null) {
            log.warn { "$host can't resolve city." }
        }
    }

    @ParameterizedTest(name = "find city for private ip {0}")
    @ValueSource(strings = ["172.30.1.22"])
    fun `find city by private ip address is not support`(host: String) {
        val ipAddress = InetAddress.getByName(host)
        val address = cityFinder.findAddress(ipAddress)
        address.shouldBeNull()
    }
}
