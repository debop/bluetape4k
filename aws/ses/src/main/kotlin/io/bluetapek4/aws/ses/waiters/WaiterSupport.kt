package io.bluetapek4.aws.ses.waiters

import software.amazon.awssdk.core.retry.backoff.BackoffStrategy
import software.amazon.awssdk.core.waiters.WaiterOverrideConfiguration
import java.time.Duration

inline fun waiterOverrideConfiguration(
    initializer: WaiterOverrideConfiguration.Builder.() -> Unit,
): WaiterOverrideConfiguration {
    return WaiterOverrideConfiguration.builder().apply(initializer).build()
}

fun waiterOverrideConfigurationOf(
    maxAttempts: Int = 3,
    waitTimeout: Duration = Duration.ofSeconds(5),
    backoffStrategy: BackoffStrategy = BackoffStrategy.defaultStrategy(),
): WaiterOverrideConfiguration = waiterOverrideConfiguration {
    this.backoffStrategy(backoffStrategy)
    this.maxAttempts(maxAttempts)
    this.waitTimeout(waitTimeout)
}