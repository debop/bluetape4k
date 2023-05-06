package io.bluetape4k.data.hibernate.reactive

import io.bluetape4k.logging.KLogging
import net.datafaker.Faker
import javax.persistence.EntityManagerFactory
import javax.persistence.Persistence

abstract class AbstractHibernateReactiveTest {

    companion object: KLogging() {
        val faker = Faker()
    }

    protected fun getEntityManagerFacotry(): EntityManagerFactory {
        val props = MySQLLauncher.hibernateProperties
        return Persistence.createEntityManagerFactory("default", props)
    }
}
