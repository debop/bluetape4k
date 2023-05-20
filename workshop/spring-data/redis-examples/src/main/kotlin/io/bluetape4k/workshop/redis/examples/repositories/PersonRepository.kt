package io.bluetape4k.workshop.redis.examples.repositories

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.geo.Circle
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.QueryByExampleExecutor

interface PersonRepository: CrudRepository<Person, String>, QueryByExampleExecutor<Person> {

    fun findAllByLastname(lastname: String): List<Person>

    fun findAllByLastname(lastname: String, pageable: Pageable): Page<Person>

    fun findAllByFirstnameAndLastname(firstname: String, lastname: String): List<Person>

    fun findAllByFirstnameOrLastname(firstname: String, lastname: String): List<Person>

    fun findAllByAddress_City(city: String): List<Person>

    fun findByAddress_LocationWithin(circule: Circle): List<Person>

    // NOTE: Redis 에서는 Reference 나 Reference 의 속성으로는 조회하는 것은 지원하지 않는다 (Embedded 는 지원)
    fun findByChildren_Firstname(firstname: String): List<Person>
}
