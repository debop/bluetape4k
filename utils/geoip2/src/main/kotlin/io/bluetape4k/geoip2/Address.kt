package io.bluetape4k.geoip2

import com.maxmind.geoip2.model.CityResponse
import com.maxmind.geoip2.model.CountryResponse
import com.maxmind.geoip2.record.Traits
import java.io.Serializable
import java.net.InetAddress

data class Address(
    val ipAddress: String? = null,
    val city: String? = null,
    val country: String? = null,
    val continent: String? = null,
    val geoLocation: GeoLocation? = null,
    val countryIsoCode: String? = null,
    val traits: Traits? = null,
): Serializable {

    companion object {

        @JvmStatic
        fun fromCity(ipAddress: InetAddress, cityResponse: CityResponse): Address {
            return Address(
                ipAddress = ipAddress.toString(),
                city = cityResponse.city.name,
                country = cityResponse.country.name,
                continent = cityResponse.continent.name,
                geoLocation = GeoLocation.fromLocation(cityResponse.location),
                countryIsoCode = cityResponse.country.isoCode,
                traits = cityResponse.traits
            )
        }

        @JvmStatic
        fun fromCountry(ipAddress: InetAddress, countryResponse: CountryResponse): Address {
            return Address(
                ipAddress = ipAddress.toString(),
                country = countryResponse.country.name,
                continent = countryResponse.continent.name,
                countryIsoCode = countryResponse.country.isoCode,
                traits = countryResponse.traits
            )
        }
    }
}
