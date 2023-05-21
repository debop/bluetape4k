package io.bluetape4k.workshop.redis.examples.commands

import io.bluetape4k.logging.debug
import io.bluetape4k.workshop.redis.examples.AbstractRedisTest
import org.amshove.kluent.shouldBeInRange
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldHaveSize
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.redis.connection.RedisGeoCommands
import org.springframework.data.redis.core.GeoOperations
import org.springframework.data.redis.core.RedisOperations

class GeoOperationsTest(
    @Autowired private val operations: RedisOperations<String, String>
) : AbstractRedisTest() {

    private val geoOperations: GeoOperations<String, String>
        get() = operations.opsForGeo()

    @BeforeEach
    fun setup() {
        geoOperations.add("Sicily", Point(13.361389, 38.115556), "Arigento")
        geoOperations.add("Sicily", Point(15.087269, 37.502669), "Catania")
        geoOperations.add("Sicily", Point(13.583333, 37.316667), "Palermo")
    }

    @Test
    fun `반경 안에 있는 도시 찾기`() {
        val byDistance = geoOperations.radius(
            "Sicily",
            "Palermo",
            Distance(100.0, RedisGeoCommands.DistanceUnit.KILOMETERS)
        )!!
        byDistance shouldHaveSize 2
        byDistance.content.map { it.content.name } shouldContainSame listOf("Arigento", "Palermo")

        val greaterDistance = geoOperations.radius(
            "Sicily",
            "Palermo",
            Distance(200.0, RedisGeoCommands.DistanceUnit.KILOMETERS)
        )!!
        greaterDistance shouldHaveSize 3
        greaterDistance.content.map { it.content.name } shouldContainSame listOf("Arigento", "Catania", "Palermo")
    }

    @Test
    fun `특정 지정의 Circle을 활용하여 도시 구하기`() {
        val circle = Circle(
            Point(13.583333, 37.316667),
            Distance(100.0, RedisGeoCommands.DistanceUnit.KILOMETERS)
        )
        val result = geoOperations.radius("Sicily", circle)!!

        result shouldHaveSize 2
        result.content.map { it.content.name } shouldContainSame listOf("Arigento", "Palermo")
    }

    @Test
    fun `두 지점의 거리 구하기`() {
        val distance: Distance = geoOperations.distance(
            "Sicily",
            "Catania",
            "Palermo",
            RedisGeoCommands.DistanceUnit.KILOMETERS
        )!!
        distance.value shouldBeInRange 130.0..140.0
    }

    @Test
    fun `두 지점의 geohash 구하기`() {
        val geohashes: List<String> = geoOperations.hash("Sicily", "Catania", "Palermo")!!

        log.debug { "geohashes=${geohashes.joinToString(",")}" }
        geohashes shouldHaveSize 2
        geohashes shouldContainSame listOf("sqdtr74hyu0", "sq9sm1716e0")
    }
}
