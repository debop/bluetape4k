package io.bluetape4k.utils

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.SynchronizedObject

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
    private val synchronizedObject = SynchronizedObject()

    fun getInstance(): T {
        val result: T? = instance.value
        return result ?: synchronized(synchronizedObject) {
            instance.getAndSet(_factory?.invoke())
            instance.value!!
        }
    }
}
