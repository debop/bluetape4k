package io.bluetape4k.coroutines.flow.extensions.utils


/**
 * This value is used a a surrogate `null` value when needed.
 * It should never leak to the outside world.
 * Its usage typically are paired with [Symbol.unbox] usages.
 */
@JvmField
internal val NULL_VALUE = Symbol("NULL")

/**
 * Symbol to indicate that the value is not yet initialized.
 * It should never leak to the outside world.
 */
@JvmField
internal val UNINITIALIZED = Symbol("UNINITIALIZED")

/*
 * Symbol used to indicate that the flow is complete.
 * It should never leak to the outside world.
 */
@JvmField
internal val DONE_VALUE = Symbol("DONE")
