package io.bluetapek4.aws.ses.waiters

import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration
import software.amazon.awssdk.services.ses.SesClient
import software.amazon.awssdk.services.ses.waiters.SesWaiter

inline fun sesWaiter(initializer: SesWaiter.Builder.() -> Unit): SesWaiter {
    return SesWaiter.builder().apply(initializer).build()
}

fun sesWaiterOf(
    client: SesClient,
    configuration: WaiterOverrideConfiguration = waiterOverrideConfigurationOf(),
): SesWaiter = sesWaiter {
    client(client)
    overrideConfiguration(configuration)
}
