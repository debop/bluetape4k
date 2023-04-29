package io.bluetape4k.infra.otel.metrics

import io.opentelemetry.api.metrics.MeterProvider
import io.opentelemetry.sdk.metrics.SdkMeterProvider
import io.opentelemetry.sdk.metrics.SdkMeterProviderBuilder

/**
 * 무기록, 무출력 하는 meter 들을 제공하는 [MeterProvider]
 */
@JvmField
val NoopMeterProvider: MeterProvider = MeterProvider.noop()

/**
 * [SdkMeterProvider] 를 생성합니다.
 *
 * @param initializer [SdkMeterProviderBuilder] 를 설정하는 람다입니다.
 * @return [SdkMeterProvider] 인스턴스
 */
inline fun sdkMeterProvider(initializer: SdkMeterProviderBuilder.() -> Unit): SdkMeterProvider {
    return SdkMeterProvider.builder().apply(initializer).build()
}
