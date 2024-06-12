package io.bluetape4k.okio.coroutines

import io.bluetape4k.okio.coroutines.internal.ForwardingAsyncSink
import io.bluetape4k.okio.coroutines.internal.ForwardingSink
import kotlinx.coroutines.Dispatchers
import okio.Sink
import kotlin.coroutines.CoroutineContext

fun Sink.toAsync(context: CoroutineContext = Dispatchers.IO): AsyncSink {
    if (this is ForwardingSink) return this.delegate
    return ForwardingAsyncSink(this, context)
}

fun AsyncSink.toBlocking(): Sink {
    if (this is ForwardingAsyncSink) return this.delegate
    return ForwardingSink(this)
}
