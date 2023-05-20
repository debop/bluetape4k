package io.bluetape4k.workshop.redis.examples.repositories

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.toUtf8Bytes
import io.bluetape4k.workshop.redis.examples.AbstractRedisTest
import io.bluetape4k.workshop.redis.examples.utils.buildExampleMatcher
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContain
import org.amshove.kluent.shouldContainSame
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotContain
import org.amshove.kluent.shouldNotContainAny
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Example
import org.springframework.data.domain.ExampleMatcher
import org.springframework.data.domain.PageRequest
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.redis.core.RedisOperations
import org.springframework.data.repository.findByIdOrNull

class PersonRepositoryTest @Autowired constructor(
    private val operations: RedisOperations<Any?, Any?>,
    private val repository: PersonRepository,
): AbstractRedisTest() {

    companion object: KLogging() {
        private val CHARSET = Charsets.UTF_8
    }

    /*
	 * Set of test users
	 */
    private val eddard = Person("eddard", "stark", Gender.MALE)
    private val robb = Person("robb", "stark", Gender.MALE)
    private val sansa = Person("sansa", "stark", Gender.FEMALE)
    private val arya = Person("arya", "stark", Gender.FEMALE)
    private val bran = Person("bran", "stark", Gender.MALE)
    private val rickon = Person("rickon", "stark", Gender.MALE)
    private val jon = Person("jon", "snow", Gender.MALE)

    val users = listOf(eddard, robb, sansa, arya, bran, rickon, jon)

    private fun saveTestUsers() {
        repository.saveAll(users)
    }

    @BeforeEach
    fun beforeEach() {
        operations.execute { conn ->
            conn.flushDb()
            "OK"
        }
    }

    @Test
    fun `context loading`() {
        operations.shouldNotBeNull()
        repository.shouldNotBeNull()
    }

    @Test
    fun `save single entity`() {
        repository.save(eddard)

        operations.execute { conn ->
            conn.exists("persons:${eddard.id}".toUtf8Bytes())!!.shouldBeTrue()
        }
    }

    @Test
    fun `find by single indexed property`() {
        saveTestUsers()

        val starks = repository.findAllByLastname(eddard.lastname)
        starks shouldContainSame listOf(eddard, robb, sansa, arya, bran, rickon) shouldNotContain jon
    }

    @Test
    fun `find by multiple indexed properties`() {
        saveTestUsers()

        val aryaStark = repository.findAllByFirstnameAndLastname(arya.firstname, arya.lastname)
        aryaStark shouldContainSame listOf(arya)
    }

    /**
     * Kotlin 클래스에 대해서 non-null 때문에 Example 만드는 것을 수작업으로 해야 한다!!!
     *
     * 참고: [Kotlinic pattern for using Spring Data JPA's "query by example"](https://stackoverflow.com/questions/45010230/kotlinic-pattern-for-using-spring-data-jpas-query-by-example)
     */
    @Test
    fun `find by query by example`() {
        saveTestUsers()

        // Kotlin 클래스에 대해서 non-null 때문에 Example 만드는 것을 이렇게 Example에 지정할 속성명을 특정해주는 [ExampleMatcher]를 사용해야 한다!!!
        val matcher = Person::class
            .buildExampleMatcher(Person::lastname.name)
            .withMatcher(Person::lastname.name, ExampleMatcher.GenericPropertyMatchers.exact())
            .withIgnoreNullValues()

        val personExample = Person("", arya.lastname)
        val founds = repository.findAll(Example.of(personExample, matcher))
        log.debug { "example search for starks: $founds" }

        founds shouldContain arya shouldNotContain jon
    }

    @Test
    fun `find all in range with Pageable`() {
        saveTestUsers()

        val page1 = repository.findAllByLastname(eddard.lastname, PageRequest.of(0, 5))
        page1.numberOfElements shouldBeEqualTo 5
        page1.totalElements shouldBeEqualTo 6

        val page2 = repository.findAllByLastname(eddard.lastname, PageRequest.of(1, 5))
        page2.numberOfElements shouldBeEqualTo 1
        page2.totalElements shouldBeEqualTo 6
    }

    @Test
    fun `find by embedded property`() {
        val winterfell = Address(city = "winterfell", country = "the north")

        eddard.address = winterfell
        repository.save(eddard)

        val eddarStark = repository.findAllByAddress_City(winterfell.city)
        eddarStark shouldContainSame listOf(eddard)
    }

    @Test
    fun `find by geo location property`() {
        val winterfell = Address(city = "winterfell", country = "the north", Point(52.9541053, -1.2401016))

        eddard.address = winterfell
        repository.save(eddard)

        val casterlystein = Address("Casterlystein", "Westerland", Point(51.5287352, -0.3817819))
        robb.address = casterlystein
        repository.save(robb)

        val smallCircle = Circle(Point(51.8911912, -0.4979756), Distance(50.0, Metrics.KILOMETERS))
        val smallPersons = repository.findByAddress_LocationWithin(smallCircle)
        smallPersons shouldContainSame listOf(robb)

        val largeCircle = Circle(Point(51.8911912, -0.4979756), Distance(200.0, Metrics.KILOMETERS))
        val largePersons = repository.findByAddress_LocationWithin(largeCircle)
        largePersons shouldContainSame listOf(eddard, robb)
    }

    /**
     * Store references to other entities without embedding all data.
     *
     * Print out the hash structure within Redis.
     */
    @Test
    fun `use references to store data to other objects`() {
        eddard.children.clear()
        eddard.children.addAll(listOf(jon, robb, sansa, arya, bran, rickon))
        saveTestUsers()

        repository.findByIdOrNull(eddard.id!!)!!.children shouldContainSame listOf(jon, robb, sansa, arya, bran, rickon)

        /**
         * NOTE: 자식 Refrenece를 삭제하면 부모의 children에서도 삭제된다.
         */
        repository.deleteAll(listOf(robb, jon))

        repository.findByIdOrNull(eddard.id!!)!!
            .children shouldContainSame listOf(sansa, arya, bran, rickon) shouldNotContainAny listOf(robb, jon)
    }

    @Disabled("@Refernce가 적용된 속성으로 찾기는 지원하지 않습니다")
    @Test
    fun `find person by child's firstname`() {
        saveTestUsers()

        eddard.children.clear()
        eddard.children.addAll(listOf(jon, robb, sansa, arya, bran, rickon))
        repository.save(eddard)

        repository.findByIdOrNull(eddard.id!!)!!.children shouldContainSame listOf(jon, robb, sansa, arya, bran, rickon)

        // Reference 속성으로 조회하는 기능은 제공하지 않습니다.
        val parent = repository.findByChildren_Firstname(robb.firstname)
        log.debug { "parent: $parent" }
        parent shouldContainSame listOf(eddard)
    }
}
