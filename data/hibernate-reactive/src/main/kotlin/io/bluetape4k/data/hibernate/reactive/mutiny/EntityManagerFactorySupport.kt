package io.bluetape4k.data.hibernate.reactive.mutiny

import javax.persistence.EntityManagerFactory
import org.hibernate.reactive.mutiny.Mutiny

fun EntityManagerFactory.asMutinySessionFactory(): Mutiny.SessionFactory {
    return unwrap(Mutiny.SessionFactory::class.java)
}
