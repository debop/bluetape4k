package io.bluetape4k.data.hibernate.reactive.mutiny

import io.bluetape4k.vertx.currentVertxDispatcher
import io.smallrye.mutiny.coroutines.asUni
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import org.hibernate.reactive.mutiny.Mutiny

suspend fun <T> Mutiny.SessionFactory.withSessionSuspending(
    work: suspend (session: Mutiny.Session) -> T,
): T = coroutineScope {
    withSession { session: Mutiny.Session ->
        async(currentVertxDispatcher()) {
            work(session)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withSessionSuspending(
    tenantId: String,
    work: suspend (session: Mutiny.Session) -> T,
): T = coroutineScope {
    withSession(tenantId) { session: Mutiny.Session ->
        async(currentVertxDispatcher()) {
            work(session)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withStatelessSessionSuspending(
    work: suspend (session: Mutiny.StatelessSession) -> T,
): T = coroutineScope {
    withStatelessSession { stateless: Mutiny.StatelessSession ->
        async(currentVertxDispatcher()) {
            work(stateless)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withStatelessSessionSuspending(
    tenantId: String,
    work: suspend (stateless: Mutiny.StatelessSession) -> T,
): T = coroutineScope {
    withStatelessSession(tenantId) { stateless: Mutiny.StatelessSession ->
        async(currentVertxDispatcher()) {
            work(stateless)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withTransactionSuspending(
    work: suspend (session: Mutiny.Session) -> T,
): T = coroutineScope {
    withTransaction { session: Mutiny.Session ->
        async(currentVertxDispatcher()) {
            work(session)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withTransactionSuspending(
    work: suspend (session: Mutiny.Session, trasaction: Mutiny.Transaction) -> T,
): T = coroutineScope {
    withTransaction { session: Mutiny.Session, transaction: Mutiny.Transaction ->
        async(currentVertxDispatcher()) {
            work(session, transaction)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withTransactionSuspending(
    tenantId: String,
    work: suspend (session: Mutiny.Session, trasaction: Mutiny.Transaction) -> T,
): T = coroutineScope {
    withTransaction(tenantId) { session: Mutiny.Session, transaction: Mutiny.Transaction ->
        async(currentVertxDispatcher()) {
            work(session, transaction)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withStatelessTransactionSuspending(
    work: suspend (session: Mutiny.StatelessSession) -> T,
): T = coroutineScope {
    withStatelessTransaction { stateless: Mutiny.StatelessSession ->
        async(currentVertxDispatcher()) {
            work(stateless)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withStatelessTransactionSuspending(
    work: suspend (session: Mutiny.StatelessSession, trasaction: Mutiny.Transaction) -> T,
): T = coroutineScope {
    withStatelessTransaction { stateless: Mutiny.StatelessSession, transaction: Mutiny.Transaction ->
        async(currentVertxDispatcher()) {
            work(stateless, transaction)
        }.asUni()
    }.awaitSuspending()
}

suspend fun <T> Mutiny.SessionFactory.withStatelessTransactionSuspending(
    tenantId: String,
    work: suspend (session: Mutiny.StatelessSession, trasaction: Mutiny.Transaction) -> T,
): T = coroutineScope {
    withStatelessTransaction(tenantId) { stateless: Mutiny.StatelessSession, transaction: Mutiny.Transaction ->
        async(currentVertxDispatcher()) {
            work(stateless, transaction)
        }.asUni()
    }.awaitSuspending()
}
