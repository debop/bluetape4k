package io.bluetape4k.coroutines

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

@Suppress("FunctionName")
fun SilentSupervisor(parent: Job? = null): CoroutineContext =
    SupervisorJob(parent) + CoroutineExceptionHandler { _, _ -> }
