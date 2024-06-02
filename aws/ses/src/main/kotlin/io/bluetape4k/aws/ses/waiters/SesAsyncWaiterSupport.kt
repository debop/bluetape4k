package io.bluetape4k.aws.ses.waiters

import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration
import software.amazon.awssdk.services.ses.SesAsyncClient
import software.amazon.awssdk.services.ses.waiters.SesAsyncWaiter
import java.util.concurrent.ScheduledExecutorService

inline fun sesAsyncWaiter(initializer: SesAsyncWaiter.Builder.() -> Unit): SesAsyncWaiter {
    return SesAsyncWaiter.builder().apply(initializer).build()
}

fun sesAsyncWaiterOf(
    client: SesAsyncClient,
    scheduledExecutorService: ScheduledExecutorService,
    configuration: WaiterOverrideConfiguration = waiterOverrideConfigurationOf(),
): SesAsyncWaiter = sesAsyncWaiter {
    client(client)
    scheduledExecutorService(scheduledExecutorService)
    overrideConfiguration(configuration)
}
