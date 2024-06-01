package io.bluetape4k.geoip2.finder

import io.bluetape4k.concurrent.AtomicIntRoundrobin
import io.bluetape4k.geoip2.AbstractGeoipTest
import io.bluetape4k.junit5.coroutines.MultiJobTester
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.utils.Runtimex
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource
import java.net.InetAddress
import java.util.concurrent.ConcurrentHashMap

class GeoipCityFinderTest: AbstractGeoipTest() {

    companion object: KLogging()

    val cityFinder = GeoipCityFinder()

    @ParameterizedTest(name = "find city for {0}")
    @MethodSource("getIpAddresses")
    fun `find city by ip address`(host: String) {
        val ipAddress = InetAddress.getByName(host)
        val address = cityFinder.findAddress(ipAddress)

        log.debug { "find city=$address" }
        address.shouldNotBeNull()
    }

    @ParameterizedTest(name = "find city for private ip {0}")
    @ValueSource(strings = ["172.30.1.22", "localhost", "127.0.0.1", "10.220.250.139"])
    fun `find city by private ip address is not support`(host: String) {
        val ipAddress = InetAddress.getByName(host)
        val address = cityFinder.findAddress(ipAddress)
        address.shouldBeNull()
    }

    @Test
    fun `find city in multi job`() = runTest {
        val ipAddresses = getIpAddresses()
        val expected = ipAddresses.associateWith {
            cityFinder.findAddress(InetAddress.getByName(it))
        }

        val index = AtomicIntRoundrobin(ipAddresses.size)
        val resultMap = ConcurrentHashMap<String, String?>()
        MultiJobTester()
            .numJobs(2 * Runtimex.availableProcessors)
            .roundsPerJob(10)
            .add {
                val ip = ipAddresses[index.next()]
                val address = cityFinder.findAddress(InetAddress.getByName(ip))!!
                resultMap.putIfAbsent(ip, address.country)
            }
            .run()

        expected.forEach { (ip, address) ->
            log.debug { "ip=$ip, address=$address" }
            resultMap[ip]!! shouldBeEqualTo address!!.country
        }
    }
}
