package io.bluetape4k.workshop.webflux.hibernate.reactive.config

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.info
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.webflux.hibernate.reactive.AbstractHibernateReactiveTest
import org.amshove.kluent.shouldNotBeEmpty
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import javax.persistence.EntityManagerFactory

class HibernateReactiveConfigurationTest: AbstractHibernateReactiveTest() {

    companion object: KLogging()

    @Autowired
    private val emf: EntityManagerFactory = uninitialized()

    @Test
    fun `context loading`() {
        emf.shouldNotBeNull()
        emf.metamodel.entities.forEach {
            log.info { "Entity: ${it.name}" }
        }
        emf.metamodel.entities.shouldNotBeEmpty()
    }
}
