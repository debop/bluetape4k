package io.bluetape4k.data.hibernate.reactive

import io.bluetape4k.junit5.faker.Fakers
import io.bluetape4k.logging.KLogging
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

abstract class AbstractHibernateReactiveTest {

    companion object: KLogging() {
        val faker = Fakers.faker
    }

    protected fun getEntityManagerFacotry(): EntityManagerFactory {
        val props = MySQLLauncher.hibernateProperties
        return Persistence.createEntityManagerFactory("default", props)
    }
}
