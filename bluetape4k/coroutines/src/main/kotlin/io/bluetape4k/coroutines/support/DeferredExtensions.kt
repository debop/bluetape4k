package io.bluetape4k.coroutines.support

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext

/**
 * 두 개의 [Deferred]의 값을 하나의 [Deferred]로 만듭니다.
 *
 * @param src1 [Deferred] 인스턴스
 * @param src2 [Deferred] 인스턴스
 * @param coroutineStart [CoroutineStart] 값
 * @param zipper 두 [Deferred] 값의 zip 함수
 */
inline fun <T1, T2, R> CoroutineScope.zip(
    src1: Deferred<T1>,
    src2: Deferred<T2>,
    coroutineStart: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline zipper: (T1, T2) -> R,
): Deferred<R> =
    async(start = coroutineStart) {
        zipper(src1.await(), src2.await())
    }

/**
 * Deferred 의 값을 [transform]로 변환하여 새로운 Deferred 를 만듭니다.
 */
suspend inline fun <T, R> Deferred<T>.map(crossinline transform: suspend (T) -> R): Deferred<R> = coroutineScope {
    val self = this@map
    async {
        transform(self.await())
    }
}


@Deprecated("use mapAll", replaceWith = ReplaceWith("mapAll(coroutineStart, mapper)"))
suspend fun <K, T: Collection<K>, R> Deferred<T>.flatMap(
    coroutineStart: CoroutineStart = CoroutineStart.DEFAULT,
    mapper: suspend (K) -> Iterable<R>,
): Deferred<Collection<R>> = coroutineScope {
    val self = this@flatMap
    async(start = coroutineStart) {
        self.await()
            .map { withContext(coroutineContext) { mapper(it) } }
            .flatten()
    }
}

suspend inline fun <K, T: Collection<K>, R> Deferred<T>.mapAll(
    coroutineStart: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline transform: (K) -> Iterable<R>,
): Deferred<Collection<R>> = coroutineScope {
    val self = this@mapAll
    async(start = coroutineStart) {
        self.await().map { transform(it) }.flatten()
    }
}

suspend inline fun <K, T: Collection<K>, R> Deferred<T>.concatMap(
    coroutineStart: CoroutineStart = CoroutineStart.DEFAULT,
    crossinline transform: (K) -> R,
): Deferred<Collection<R>> = coroutineScope {
    val self = this@concatMap
    async(start = coroutineStart) {
        self.await().map { transform(it) }
    }
}
