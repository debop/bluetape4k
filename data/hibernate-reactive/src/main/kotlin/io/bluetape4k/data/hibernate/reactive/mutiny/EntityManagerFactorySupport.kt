package io.bluetape4k.data.hibernate.reactive.mutiny

import org.hibernate.reactive.mutiny.Mutiny
import jakarta.persistence.EntityManagerFactory

fun EntityManagerFactory.asMutinySessionFactory(): Mutiny.SessionFactory {
    return unwrap(Mutiny.SessionFactory::class.java)
}
