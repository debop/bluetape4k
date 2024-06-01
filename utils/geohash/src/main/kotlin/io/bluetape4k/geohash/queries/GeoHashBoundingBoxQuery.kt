package io.bluetape4k.geohash.queries

import io.bluetape4k.collections.exists
import io.bluetape4k.geohash.BoundingBox
import io.bluetape4k.geohash.GeoHash
import io.bluetape4k.geohash.WGS84Point
import io.bluetape4k.geohash.boundingBoxOf
import io.bluetape4k.geohash.geoHashWithBits
import io.bluetape4k.geohash.getAdjacent
import io.bluetape4k.geohash.utils.GeoHashSizeTable
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.logging.trace
import java.io.Serializable


fun geoHashBoundingBoxQueryOf(bbox: BoundingBox): GeoHashBoundingBoxQuery = GeoHashBoundingBoxQuery(bbox)

fun geoHashBoundingBoxQueryOf(
    southLatitude: Double,
    northLatitude: Double,
    westLongitude: Double,
    eastLongitude: Double,
): GeoHashBoundingBoxQuery =
    GeoHashBoundingBoxQuery(boundingBoxOf(southLatitude, northLatitude, westLongitude, eastLongitude))

/**
 * This class returns the hashes covering a certain bounding box. There are
 * either 1,2 or 4 such hashes, depending on the position of the bounding box
 * on the geohash grid.
 */
class GeoHashBoundingBoxQuery(
    private val bbox: BoundingBox,
): GeoHashQuery, Serializable {

    companion object: KLogging()

    /* there can be up to 8 hashes since it can be 2 separate queries */
    private val searchHashes = ArrayList<GeoHash>(8)

    /* the combined bounding box of those hashes. */
    private var boundingBox: BoundingBox? = null


    init {
        buildSearchHashes()
    }

    private fun buildSearchHashes() {
        log.trace { "Build search hashes for $bbox" }

        if (!bbox.isIntersection180Meridian) {
            // In this case one query is enough
            generateSearchHashes(bbox)
        } else {
            // In this case we need two queries
            val eastBox = BoundingBox(bbox.southLatitude, bbox.northLatitude, bbox.westLongitude, 180.0)
            val westBox = BoundingBox(bbox.southLatitude, bbox.northLatitude, -180.0, bbox.eastLongitude)
            generateSearchHashes(eastBox)
            generateSearchHashes(westBox)
        }

        // Finally create the combined bounding box
        for (hash in searchHashes) {
            if (boundingBox == null) boundingBox = hash.boundingBox.copy()
            else boundingBox!!.expandToInclude(hash.boundingBox)
        }

        // Check the search hashes on a query over the full planet
        for (hash in searchHashes) {
            if (hash.significantBits() == 0) {
                searchHashes.clear()
                searchHashes.add(hash)
                log.debug { "Add search hashes and stop. $hash" }
                return
            }
        }

        deduplicateHashes()
    }

    private fun deduplicateHashes() {
        // Check the search hashes on possible duplicates
        val toRemove: MutableList<GeoHash> = ArrayList(searchHashes.size - 1)

        searchHashes.forEach { hash ->
            searchHashes.forEach { hashToCompare ->
                if (hashToCompare.significantBits() < hash.significantBits()) {
                    var hashCopy = hash.longValue
                    var hashCompareCopy = hashToCompare.longValue
                    var equalBits = 0
                    while ((hashCompareCopy and GeoHash.FIRST_BIT_FLAGGED) == (hashCopy and GeoHash.FIRST_BIT_FLAGGED)) {
                        hashCompareCopy = hashCompareCopy shl 1
                        hashCopy = hashCopy shl 1
                        equalBits++
                    }
                    if (equalBits == hashToCompare.significantBits()) {
                        toRemove.add(hashToCompare)
                    }
                }
            }
        }
        toRemove.forEach { hash ->
            searchHashes.remove(hash)
            log.trace { "Remove duplicated search hash. $hash" }
        }
    }

    private fun generateSearchHashes(bbox: BoundingBox) {
        val fittingBits = GeoHashSizeTable.numberOfBitsForOverlappingGeoHash(bbox)
        val center = bbox.getCenter()
        val centerHash = geoHashWithBits(center.latitude, center.longitude, fittingBits)

        if (hashContainsBoundingBox(centerHash, bbox)) {
            // If the centerHash completly fits into the provided bounding box, just add the hash and continue
            searchHashes.add(centerHash)
            log.trace { "Add center hash. $centerHash" }
        } else {
            // Else the search has to be extended to the adjacent geohashes
            expandSearch(centerHash, bbox)
        }
    }

    private fun expandSearch(centerHash: GeoHash, bbox: BoundingBox) {
        searchHashes.add(centerHash)
        log.trace { "Add expand center hashes. $centerHash" }

        for (adjacent in centerHash.getAdjacent()) {
            log.trace { "Check adjacent hash. $adjacent" }
            if (!searchHashes.contains(adjacent) && adjacent.boundingBox.intersects(bbox)) {
                searchHashes.add(adjacent)
                log.trace { "Add adjacent hash. $adjacent" }
            }
        }
    }

    /**
     * Checks if the provided hash completely(!) contains the provided bounding box
     */
    private fun hashContainsBoundingBox(hash: GeoHash, bbox: BoundingBox): Boolean {
        return hash.contains(bbox.getNorthWestCorner()) && hash.contains(bbox.getSouthEastCorner())
    }

    override operator fun contains(hash: GeoHash): Boolean {
        return searchHashes.exists { hash.within(it) }
    }

    override operator fun contains(point: WGS84Point): Boolean {
        return contains(geoHashWithBits(point.latitude, point.longitude, 64))
    }

    override fun getSearchHashes(): List<GeoHash> {
        return searchHashes.toList()
    }

    override fun toString(): String = buildString {
        for (hash in searchHashes) {
            appendLine(hash)
        }
    }

    override fun getWktBox(): String {
        return "BOX(${boundingBox?.westLongitude} ${boundingBox?.southLatitude}, ${boundingBox?.eastLongitude} ${boundingBox?.northLatitude})"
    }
}
