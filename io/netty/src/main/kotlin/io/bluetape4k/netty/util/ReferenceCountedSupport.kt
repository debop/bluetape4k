package io.bluetape4k.netty.util

import io.netty.util.ReferenceCounted

/**
 * Use the [ReferenceCounted] object in the block and release it after the block is executed.
 *
 * @param decrement the number of times to decrement the reference count
 * @param block the block to execute
 * @receiver
 */
inline fun <T: ReferenceCounted> T.use(decrement: Int = 1, block: (T) -> Unit) {
    try {
        block(this)
    } finally {
        release(decrement)
    }
}
