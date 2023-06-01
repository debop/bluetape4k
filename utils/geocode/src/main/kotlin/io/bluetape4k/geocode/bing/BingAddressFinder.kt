package io.bluetape4k.geocode.bing

import io.bluetape4k.geocode.Address
import io.bluetape4k.geocode.Geocode
import io.bluetape4k.geocode.GeocodeAddressFinder
import io.bluetape4k.geocode.bing.BingMapModel.toBingAddress
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug

class BingAddressFinder : GeocodeAddressFinder {

    companion object : KLogging()

    private val client by lazy {
        BingMapService.getBingMapFeignClient()
    }

    private val coroutineClient by lazy {
        BingMapService.getBingMapFeignCoroutineClient()
    }

    override fun findAddress(geocode: Geocode, language: String): Address? {
        return runCatching {
            val location = client.locations(
                latitude = geocode.latitude.toDouble(),
                longitude = geocode.longitude.toDouble(),
            )
            log.debug { "location=$location" }
            location.toBingAddress()
        }.getOrNull()
    }

    override suspend fun findAddressAsync(geocode: Geocode, language: String): Address? {
        val location = coroutineClient.locations(
            latitude = geocode.latitude.toDouble(),
            longitude = geocode.longitude.toDouble(),
        )
        log.debug { "location=$location" }
        return location.toBingAddress()
    }
}
