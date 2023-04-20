package io.bluetape4k.data.hibernate.reactive.mutiny

import io.bluetape4k.vertx.currentVertxDispatcher
import io.smallrye.mutiny.coroutines.asUni
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.hibernate.reactive.mutiny.Mutiny

suspend fun <T> Mutiny.SessionFactory.withSessionAndAwait(
    work: suspend (session: Mutiny.Session) -> T,
): T = coroutineScope {
    withSession { session: Mutiny.Session ->
        async(currentVertxDispatcher()) {
            work(session)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withSessionAndAwait(
    tenantId: String,
    work: suspend (session: Mutiny.Session) -> T,
): T = coroutineScope {
    withSession(tenantId) { session: Mutiny.Session ->
        async(currentVertxDispatcher()) {
            work(session)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withStatelessSessionAndAwait(
    work: suspend (session: Mutiny.StatelessSession) -> T,
): T = coroutineScope {
    withStatelessSession { stateless: Mutiny.StatelessSession ->
        async(currentVertxDispatcher()) {
            work(stateless)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withStatelessSessionAndAwait(
    tenantId: String,
    work: suspend (stateless: Mutiny.StatelessSession) -> T,
): T = coroutineScope {
    withStatelessSession(tenantId) { stateless: Mutiny.StatelessSession ->
        async(currentVertxDispatcher()) {
            work(stateless)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withTransactionAndAwait(
    work: suspend (session: Mutiny.Session) -> T,
): T = coroutineScope {
    withTransaction { session: Mutiny.Session ->
        async(currentVertxDispatcher()) {
            work(session)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withTransactionAndAwait(
    work: suspend (session: Mutiny.Session, trasaction: Mutiny.Transaction) -> T,
): T = coroutineScope {
    withTransaction { session: Mutiny.Session, transaction: Mutiny.Transaction ->
        async(currentVertxDispatcher()) {
            work(session, transaction)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withTransactionAndAwait(
    tenantId: String,
    work: suspend (session: Mutiny.Session, trasaction: Mutiny.Transaction) -> T,
): T = coroutineScope {
    withTransaction(tenantId) { session: Mutiny.Session, transaction: Mutiny.Transaction ->
        async(currentVertxDispatcher()) {
            work(session, transaction)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withStatelessTransactionAndAwait(
    work: suspend (session: Mutiny.StatelessSession) -> T,
): T = coroutineScope {
    withStatelessTransaction { stateless: Mutiny.StatelessSession ->
        async(currentVertxDispatcher()) {
            work(stateless)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withStatelessTransactionAndAwait(
    work: suspend (session: Mutiny.StatelessSession, trasaction: Mutiny.Transaction) -> T,
): T = coroutineScope {
    withStatelessTransaction { stateless: Mutiny.StatelessSession, transaction: Mutiny.Transaction ->
        async(currentVertxDispatcher()) {
            work(stateless, transaction)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withStatelessTransactionAndAwait(
    tenantId: String,
    work: suspend (session: Mutiny.StatelessSession, trasaction: Mutiny.Transaction) -> T,
): T = coroutineScope {
    withStatelessTransaction(tenantId) { stateless: Mutiny.StatelessSession, transaction: Mutiny.Transaction ->
        async(currentVertxDispatcher()) {
            work(stateless, transaction)
        }.asUni()
    }.awaitSuspending()
}
