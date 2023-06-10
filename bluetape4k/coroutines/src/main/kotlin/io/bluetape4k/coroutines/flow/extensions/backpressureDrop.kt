@file:JvmMultifileClass
@file:JvmName("FlowExtensionsKt")

package io.bluetape4k.coroutines.flow.extensions

import io.bluetape4k.coroutines.flow.extensions.internal.FlowOnBackpressureDrop
import kotlinx.coroutines.flow.Flow

/**
 * Drops items from the upstream when the downstream is not ready to receive them.
 */
fun <T> Flow<T>.onBackpressureDrop(): Flow<T> = FlowOnBackpressureDrop(this)
