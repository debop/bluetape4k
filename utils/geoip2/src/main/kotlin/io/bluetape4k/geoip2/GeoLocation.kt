package io.bluetape4k.geoip2

import com.maxmind.geoip2.record.Location
import java.io.Serializable

data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val timeZone: String? = null,
    val accuracyRadius: Int? = null,
    val metroCode: Int? = null,
): Serializable {

    companion object {
        @JvmStatic
        fun fromLocation(location: Location): GeoLocation {
            return GeoLocation(
                latitude = location.latitude,
                longitude = location.longitude,
                timeZone = location.timeZone,
                accuracyRadius = location.accuracyRadius,
                metroCode = location.metroCode
            )
        }
    }
}
