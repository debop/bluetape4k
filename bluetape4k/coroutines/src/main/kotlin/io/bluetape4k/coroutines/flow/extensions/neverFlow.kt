@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

sealed interface NeverFlow: Flow<Nothing> {

    companion object: NeverFlow {
        override suspend fun collect(collector: FlowCollector<Nothing>) = awaitCancellation()
    }

    override suspend fun collect(collector: FlowCollector<Nothing>)
}

fun neverFlow(): NeverFlow = NeverFlow
