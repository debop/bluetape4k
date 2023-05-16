package io.bluetape4k.quarkus.kotlin.panache

import io.bluetape4k.utils.mutiny.asUni
import io.bluetape4k.vertx.currentVertxDispatcher
import io.quarkus.hibernate.reactive.panache.Panache
import io.quarkus.panache.common.Parameters
import io.smallrye.mutiny.Uni
import io.smallrye.mutiny.coroutines.awaitSuspending
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.hibernate.reactive.mutiny.Mutiny

/**
 * [Panache] 를 이용하여 Transaction 하에서 [work] 를 수행합니다.
 *
 * ```
 * val fruit = withPanacheTransaction { session, tx ->
 *      repository
 *          .persist(newGrape())
 *          .replaceWith(repository.findByName(grape.name))
 * }.await().indefinitely()
 * ```
 */
fun <T> withPanacheTransaction(
    work: (session: Mutiny.Session, tx: Mutiny.Transaction) -> Uni<T>,
): Uni<T> {
    return Panache.getSession().chain { session ->
        session.withTransaction { tx ->
            work(session, tx)
        }
    }
}

/**
 * [Panache] 를 이용하여 Transaction 하에서 suspend 함수인 [work] 를 수행합니다.
 *
 * ```
 * val fruit = withPanacheTransactionSuspending { session, tx ->
 *      repository.persist(newGrape()).awaitSuspending()
 *      repository.findByName(grape.name).awaitSuspending()
 * }
 * ```
 */
suspend fun <T> withPanacheTransactionSuspending(
    work: suspend (session: Mutiny.Session, tx: Mutiny.Transaction) -> T,
): T = coroutineScope {
    Panache.getSession().chain { session ->
        session.withTransaction { tx ->
            asUni(currentVertxDispatcher()) {
                work(session, tx)
            }
        }
    }.awaitSuspending()
}


/**
 * [Panache] 를 이용하여 Transaction 하에서 [work] 를 수행해봅니다. 실행 완료 후 rollback 을 수행합니다.
 * 테스트 시에 사용하면, 작업 검증만을 수행하고, 대상 DB의 데이터 상태는 변화를 주지 않습니다.
 *
 * ```
 * val fruit = withPanacheRollback { session, tx ->
 *      repository.persist(newGrape())
 *      .replaceWith(repository.findByName(grape.name))
 * }.await().indefinitely()
 * ```
 */
fun <T> withPanacheRollback(
    work: (session: Mutiny.Session, tx: Mutiny.Transaction) -> Uni<T>,
): Uni<T> {
    return withPanacheTransaction { session, tx ->
        tx.markForRollback()
        work(session, tx)
    }
}

/**
 * [Panache] 를 이용하여 Transaction 하에서 suspend 함수인 [work] 를 수행해봅니다. 실행 완료 후 rollback 을 수행합니다.
 * 테스트 시에 사용하면, 작업 검증만을 수행하고, 대상 DB의 데이터 상태는 변화를 주지 않습니다.
 *
 * ```
 * val fruit = withPanacheRollbackSuspending { session, tx ->
 *      repository.persist(newGrape()).awaitSuspending()
 *      repository.findByName(grape.name).awaitSuspending()
 * }
 * ```
 */
suspend fun <T> withPanacheRollbackSuspending(
    work: suspend (session: Mutiny.Session, tx: Mutiny.Transaction) -> T,
): T = withPanacheTransactionSuspending { session, tx ->
    tx.markForRollback()
    work(session, tx)
}

suspend fun <T> executeUpdateSuspending(query: String, vararg params: Any?): Int = coroutineScope {
    withContext(currentVertxDispatcher()) {
        Panache.executeUpdate(query, *params).awaitSuspending()
    }
}

suspend fun <T> executeUpdateSuspending(query: String, params: Map<String, Any?>): Int = coroutineScope {
    withContext(currentVertxDispatcher()) {
        Panache.executeUpdate(query, params).awaitSuspending()
    }
}

suspend fun <T> executeUpdateSuspending(query: String, params: Parameters): Int = coroutineScope {
    withContext(currentVertxDispatcher()) {
        Panache.executeUpdate(query, params).awaitSuspending()
    }
}
