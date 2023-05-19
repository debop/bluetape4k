package io.bluetape4k.workshop.webflux.hibernate.reactive.repository

import io.bluetape4k.data.hibernate.reactive.mutiny.asMutinySessionFactory
import io.bluetape4k.data.hibernate.reactive.mutiny.createQueryAs
import io.bluetape4k.data.hibernate.reactive.mutiny.getAs
import io.bluetape4k.data.hibernate.reactive.mutiny.withSessionSuspending
import io.bluetape4k.data.hibernate.reactive.mutiny.withStatelessSessionSuspending
import io.bluetape4k.data.hibernate.reactive.mutiny.withTransactionSuspending
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.City
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.Customer
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import org.hibernate.LockMode
import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import javax.persistence.EntityManagerFactory
import javax.persistence.criteria.CriteriaBuilder
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Join
import javax.persistence.criteria.Root

@Repository
class CustomerRepository(@Autowired emf: EntityManagerFactory) {

    val sf: Mutiny.SessionFactory = emf.asMutinySessionFactory()

    val criteriaBuilder: CriteriaBuilder get() = sf.criteriaBuilder

    suspend fun <T> persist(entity: T): T {
        return sf.withTransactionSuspending { session ->
            session.persist(entity).replaceWith(entity).awaitSuspending()
        }
    }

    suspend fun findById(id: Long): Customer? {
        return sf.withStatelessSessionSuspending { stateless ->
            stateless.getAs<Customer>(id).awaitSuspending()
        }
    }

    suspend fun findAll(): List<Customer> {
        //        return sf.withStatelessSessionAndAwait { stateless: StatelessSession ->
        //            stateless.createQueryAs<Customer>("from Customer").resultList.awaitSuspending()
        //        }
        // NOTE: SELECT + 1 문제를 해결하기 위해 Fetch Profile 을 수행한다면 StatelessSession 말고, Session 을 사용해야 합니다.
        // 아니면 Entity Graph 를 사용하던가
        return sf.withSessionSuspending { session: Mutiny.Session ->
            session.enableFetchProfile("withCity")
                .createQueryAs<Customer>("from Customer")
                .resultList
                .awaitSuspending()
        }
    }

    suspend fun findByCity(cityToMatch: String): List<Customer> {
        val cb = criteriaBuilder
        val query: CriteriaQuery<Customer> = cb.createQuery(Customer::class.java)
        val root: Root<Customer> = query.from(Customer::class.java)
        val city: Join<Customer, City> = root.join(Customer::city.name)
        query.where(cb.like(city.get(City::name.name), "%${cityToMatch}%"))

        return sf.withStatelessSessionSuspending { stateless: Mutiny.StatelessSession ->
            stateless.createQuery(query).resultList.awaitSuspending()
        }
    }

    suspend fun createCustomer(name: String): Customer {
        return Customer(name).apply { persist(this) }
    }

    suspend fun updateCustomer(c: Customer): Customer? {
        return sf.withStatelessSessionSuspending { stateless: Mutiny.StatelessSession ->
            stateless.update(c).replaceWith(c).awaitSuspending()
        }
    }

    suspend fun deleteCustomerById(id: Long): Customer? {
        return sf.withStatelessSessionSuspending { stateless: Mutiny.StatelessSession ->
            stateless.getAs<Customer>(id, LockMode.PESSIMISTIC_WRITE)
                .call { customer ->
                    if (customer != null) stateless.delete(customer)
                    else Uni.createFrom().nullItem<Customer>()
                }
                .awaitSuspending()
        }
    }
}
