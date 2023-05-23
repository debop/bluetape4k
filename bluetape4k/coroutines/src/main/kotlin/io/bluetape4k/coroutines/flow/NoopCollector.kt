package io.bluetape4k.coroutines.flow

import kotlinx.coroutines.flow.FlowCollector

object NoopCollector: FlowCollector<Any?> {
    override suspend fun emit(value: Any?) {
        // Nothing to do 
    }
}
