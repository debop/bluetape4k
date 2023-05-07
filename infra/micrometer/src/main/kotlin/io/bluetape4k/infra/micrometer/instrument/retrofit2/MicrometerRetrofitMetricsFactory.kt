package io.bluetape4k.infra.micrometer.instrument.retrofit2

import io.bluetape4k.logging.KLogging
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Metrics

class MicrometerRetrofitMetricsFactory private constructor(meterRegistry: MeterRegistry):
    RetrofitMetricsFactory(MicrometerMetricsRecorder(meterRegistry)) {

    companion object: KLogging() {

        @JvmOverloads
        fun create(meterRegistry: MeterRegistry = Metrics.globalRegistry): MicrometerRetrofitMetricsFactory =
            MicrometerRetrofitMetricsFactory(meterRegistry)
    }
}
