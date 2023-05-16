package io.bluetape4k.workshop.resilience4j.service.coroutines

import kotlinx.coroutines.flow.Flow

interface CoroutineService {

    suspend fun suspendSuccess(): String
    suspend fun suspendFailure(): String
    suspend fun suspendTimeout(): String

    fun flowSuccess(): Flow<String>
    fun flowFailure(): Flow<String>
    fun flowTimeout(): Flow<String>

}
