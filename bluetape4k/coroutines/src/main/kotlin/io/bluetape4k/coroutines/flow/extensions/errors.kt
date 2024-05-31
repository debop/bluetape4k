package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll

/**
 * 플로우의 완료에서 예외를 catch하고 단일 [value]를 emit한 다음 정상적으로 완료합니다.
 */
fun <T> Flow<T>.catchAndReturn(value: T): Flow<T> =
    catch { emit(value) }

/**
 * 플로우의 완료에서 예외를 catch하고 단일 [errorHandler]의 값을 emit한 다음 정상적으로 완료합니다.
 */
fun <T> Flow<T>.catchAndReturn(errorHandler: suspend (cause: Throwable) -> T): Flow<T> =
    catch { emit(errorHandler(it)) }

/**
 * 플로우의 완료에서 예외를 catch하고 [fallback] 플로우의 모든 항목을 emit합니다.
 * 만약 [fallback] 플로우도 예외를 던진다면, 예외는 catch되지 않고 다시 던져집니다.
 */
fun <T> Flow<T>.catchAndResume(fallback: Flow<T>): Flow<T> =
    catch { emitAll(fallback) }

/**
 * 플로우의 완료에서 예외를 catch하고 [fallbackHandler] 플로우의 모든 항목을 emit합니다.
 * 만약 [fallbackHandler] 플로우도 예외를 던진다면, 예외는 catch되지 않고 다시 던져집니다.
 */
fun <T> Flow<T>.catchAndResume(fallbackHandler: suspend (cause: Throwable) -> Flow<T>): Flow<T> =
    catch { emitAll(fallbackHandler(it)) }
