package io.bluetape4k.workshop.webflux.hibernate.reactive.repository

import io.bluetape4k.hibernate.reactive.mutiny.asMutinySessionFactory
import io.bluetape4k.hibernate.reactive.mutiny.createQueryAs
import io.bluetape4k.hibernate.reactive.mutiny.findAs
import io.bluetape4k.hibernate.reactive.mutiny.withSessionSuspending
import io.bluetape4k.hibernate.reactive.mutiny.withTransactionSuspending
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.City
import io.bluetape4k.workshop.webflux.hibernate.reactive.model.Customer
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import jakarta.persistence.EntityManagerFactory
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Join
import jakarta.persistence.criteria.Root
import org.hibernate.reactive.mutiny.Mutiny
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

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
        return sf.withSessionSuspending { session ->
            session.findAs<Customer>(id).awaitSuspending()
        }
//        return sf.withStatelessSessionSuspending { stateless ->
//            stateless.getAs<Customer>(id).awaitSuspending()
//        }
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

        return sf.withSessionSuspending { session: Mutiny.Session ->
            session.createQuery(query).resultList.awaitSuspending()
        }
//        return sf.withStatelessSessionSuspending { stateless: Mutiny.StatelessSession ->
//            stateless.createQuery(query).resultList.awaitSuspending()
//        }
    }

    suspend fun createCustomer(name: String): Customer {
        return Customer(name).apply { persist(this) }
    }

    suspend fun updateCustomer(c: Customer): Customer? {
        return sf.withSessionSuspending { session: Mutiny.Session ->
            session.persist(c).replaceWith(c).awaitSuspending()
        }
//        return sf.withStatelessSessionSuspending { stateless: Mutiny.StatelessSession ->
//            stateless.update(c).replaceWith(c).awaitSuspending()
//        }
    }

    suspend fun deleteCustomerById(id: Long): Customer? {
        return sf.withSessionSuspending { session: Mutiny.Session ->
            session.findAs<Customer>(id)
                .call { customer ->
                    if (customer != null) session.remove(customer)
                    else Uni.createFrom().nullItem<Customer>()
                }
                .awaitSuspending()
        }
//        return sf.withStatelessSessionSuspending { stateless: Mutiny.StatelessSession ->
//            stateless.getAs<Customer>(id, LockMode.PESSIMISTIC_WRITE)
//                .call { customer ->
//                    if (customer != null) stateless.delete(customer)
//                    else Uni.createFrom().nullItem<Customer>()
//                }
//                .awaitSuspending()
//        }
    }
}
