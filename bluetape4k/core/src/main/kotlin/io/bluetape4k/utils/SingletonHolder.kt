package io.bluetape4k.utils

import kotlinx.atomicfu.atomic
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Singleton 객체를 보관해주는 객체입니다.
 *
 * ```
 * class Manager private constructor(private val context:Context) {
 *     companion object: SingletonHolder<Manager> { Manager(context) }
 *     fun doSutff() {}
 * }
 *
 * // Use singleton
 * val manager = Manager.getInstance()
 * manager.doStuff()
 * ````
 */
open class SingletonHolder<out T: Any>(factory: () -> T) {

    private var _factory: (() -> T)? = factory
    private val instance = atomic<T?>(null)
    private val lock = ReentrantLock()

    fun getInstance(): T {
        lock.withLock {
            val result: T? = instance.value
            return result ?: run {
                instance.compareAndSet(null, _factory?.invoke())
                instance.value!!
            }
        }
    }
}
