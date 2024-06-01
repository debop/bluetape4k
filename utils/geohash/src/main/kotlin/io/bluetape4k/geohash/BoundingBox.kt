package io.bluetape4k.geohash

import io.bluetape4k.geohash.utils.remainderWithFix
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.requireInRange
import java.io.Serializable

fun boundingBoxOf(
    southWestCorner: WGS84Point,
    northEastCorner: WGS84Point,
): BoundingBox = boundingBoxOf(
    southWestCorner.latitude,
    northEastCorner.latitude,
    southWestCorner.longitude,
    northEastCorner.longitude
)

fun boundingBoxOf(
    southLatitude: Double,
    northLatitude: Double,
    westLongitude: Double,
    eastLongitude: Double,
): BoundingBox {
    southLatitude.requireInRange(-90.0, 90.0, "southLatitude")
    northLatitude.requireInRange(-90.0, 90.0, "northLatitude")
    westLongitude.requireInRange(-180.0, 180.0, "westLongitude")
    eastLongitude.requireInRange(-180.0, 180.0, "eastLongitude")

    require(southLatitude <= northLatitude) {
        "southLatitude[$southLatitude] must be less than or equal to northLatitude[$northLatitude]"
    }

    return BoundingBox(southLatitude, northLatitude, westLongitude, eastLongitude)
}


data class BoundingBox(
    var southLatitude: Double,
    var northLatitude: Double,
    var westLongitude: Double,
    var eastLongitude: Double,
): Serializable {

    companion object: KLogging()

    private var intersects180Meridian: Boolean = eastLongitude < westLongitude

    val isIntersection180Meridian: Boolean get() = intersects180Meridian

    fun getNorthEastCorner(): WGS84Point = wgs84PointOf(northLatitude, eastLongitude)
    fun getNorthWestCorner(): WGS84Point = wgs84PointOf(northLatitude, westLongitude)
    fun getSouthEastCorner(): WGS84Point = wgs84PointOf(southLatitude, eastLongitude)
    fun getSouthWestCorner(): WGS84Point = wgs84PointOf(southLatitude, westLongitude)

    fun getLatitudeSize(): Double {
        return northLatitude - southLatitude
    }

    fun getLongitudeSize(): Double {
        if (eastLongitude == 180.0 && westLongitude == -180.0) return 360.0
        var size = (eastLongitude - westLongitude) % 360

        // Remainder fix for earlier java versions
        if (size < 0) size += 360.0

        return size
    }

    fun contains(point: WGS84Point): Boolean {
        return containsLatitude(point.latitude) && containsLongitude(point.longitude)
    }

    fun intersects(other: BoundingBox): Boolean {
        // Check latitude first cause it's the same for all cases
        return if (other.southLatitude > northLatitude || other.northLatitude < southLatitude) {
            false
        } else {
            when {
                !intersects180Meridian && !other.intersects180Meridian ->
                    !(other.eastLongitude < westLongitude || other.westLongitude > eastLongitude)

                intersects180Meridian && !other.intersects180Meridian  ->
                    !(eastLongitude < other.westLongitude && westLongitude > other.eastLongitude)

                !intersects180Meridian && other.intersects180Meridian  ->
                    !(westLongitude > other.eastLongitude && eastLongitude < other.westLongitude)

                else                                                   -> true
            }
        }
    }

    fun getCenter(): WGS84Point {
        val centerLatitude = (southLatitude + northLatitude) / 2.0
        var centerLongitude = (eastLongitude + westLongitude) / 2.0
        if (centerLongitude > 180.0) {
            centerLongitude -= 360.0
        }
        return WGS84Point(centerLatitude, centerLongitude)
    }

    fun expandToInclude(point: WGS84Point) {
        // Expand Latitude
        if (point.latitude < southLatitude)
            southLatitude = point.latitude
        else if (point.latitude > northLatitude)
            northLatitude = point.latitude

        // Already done in this case
        if (containsLongitude(point.longitude)) return

        // If this is not the case compute the distance between the endpoints in east direction
        val distanceEastToPoint: Double = (point.longitude - eastLongitude).remainderWithFix(360)
        val distancePointToWest: Double = (westLongitude - point.longitude).remainderWithFix(360)

        // The minimal distance needs to be extended

        // The minimal distance needs to be extended
        if (distanceEastToPoint <= distancePointToWest)
            eastLongitude = point.longitude
        else
            westLongitude = point.longitude

        intersects180Meridian = eastLongitude < westLongitude
    }

    fun expandToInclude(other: BoundingBox) {
        // Expand Latitude
        if (other.southLatitude < southLatitude) {
            southLatitude = other.southLatitude
        } else if (other.northLatitude > northLatitude) {
            northLatitude = other.northLatitude
        }

        // Expand Longitude
        // At first check whether the two boxes contain each other or not
        val thisContainsOther = containsLongitude(other.eastLongitude) && containsLongitude(other.westLongitude)
        val otherContainsThis = other.containsLongitude(eastLongitude) && other.containsLongitude(westLongitude)

        // The new box needs to span the whole globe
        if (thisContainsOther && otherContainsThis) {
            eastLongitude = 180.0
            westLongitude = -180.0
            intersects180Meridian = false
            return
        }
        // Already done in this case
        if (thisContainsOther) return

        // Expand to match the bigger box
        if (otherContainsThis) {
            eastLongitude = other.eastLongitude
            westLongitude = other.westLongitude
            intersects180Meridian = eastLongitude < westLongitude
            return
        }

        // If this is not the case compute the distance between the endpoints in east direction
        val distanceEastToOtherEast: Double = (other.eastLongitude - eastLongitude).remainderWithFix(360)
        val distanceOtherWestToWest: Double = (westLongitude - other.westLongitude).remainderWithFix(360)

        // The minimal distance needs to be extended
        if (distanceEastToOtherEast <= distanceOtherWestToWest) {
            eastLongitude = other.eastLongitude
        } else {
            westLongitude = other.westLongitude
        }

        intersects180Meridian = eastLongitude < westLongitude
    }

    private fun containsLatitude(latitude: Double): Boolean {
        return latitude >= southLatitude && latitude <= northLatitude
    }

    private fun containsLongitude(longitude: Double): Boolean {
        return if (intersects180Meridian) {
            longitude >= westLongitude || longitude <= eastLongitude
        } else {
            longitude >= westLongitude && longitude <= eastLongitude
        }
    }
}
