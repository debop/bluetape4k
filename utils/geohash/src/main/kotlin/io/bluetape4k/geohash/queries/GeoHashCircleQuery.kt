package io.bluetape4k.geohash.queries

import io.bluetape4k.geohash.GeoHash
import io.bluetape4k.geohash.WGS84Point
import io.bluetape4k.geohash.boundingBoxOf
import io.bluetape4k.geohash.utils.VincentyGeodesy
import io.bluetape4k.logging.KLogging
import java.io.Serializable

fun geoHashCircleQueryOf(latitude: Double, longitude: Double, radius: Double): GeoHashCircleQuery =
    geoHashCircleQueryOf(WGS84Point(latitude, longitude), radius)

fun geoHashCircleQueryOf(center: WGS84Point, radius: Double): GeoHashCircleQuery {
    return GeoHashCircleQuery(center, radius)
}

/**
 * represents a radius search around a specific point via geohashes.
 * Approximates the circle with a square!
 *
 * @property center 중심 위치
 * @property radius 반경 (단위: meter)
 */
class GeoHashCircleQuery(
    private val center: WGS84Point,
    private val radius: Double,
): GeoHashQuery, Serializable {

    companion object: KLogging()

    private val query: GeoHashBoundingBoxQuery by lazy {
        val northEastCorner = VincentyGeodesy.moveInDirection(
            VincentyGeodesy.moveInDirection(center, 0.0, radius),
            90.0,
            radius
        )
        val southWestCorner = VincentyGeodesy.moveInDirection(
            VincentyGeodesy.moveInDirection(center, 180.0, radius),
            270.0,
            radius
        )
        val bbox = boundingBoxOf(southWestCorner, northEastCorner)
        GeoHashBoundingBoxQuery(bbox)
    }

    override operator fun contains(hash: GeoHash): Boolean {
        return query.contains(hash)
    }

    override operator fun contains(point: WGS84Point): Boolean {
        return query.contains(point)
    }

    override fun getWktBox(): String {
        return query.getWktBox()
    }

    override fun getSearchHashes(): List<GeoHash> {
        return query.getSearchHashes()
    }

    override fun toString(): String {
        return "Cicle Query [center=$center, radius=${getRadiusString()}]"
    }

    private fun getRadiusString(): String {
        return if (radius > 1000) {
            (radius / 1000).toString() + "km"
        } else {
            radius.toString() + "m"
        }
    }
}
