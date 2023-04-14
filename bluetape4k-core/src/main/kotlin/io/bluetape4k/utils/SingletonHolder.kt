package io.bluetape4k.utils

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
open class SingletonHolder<out T>(factory: () -> T) {

    private var _factory: (() -> T)? = factory

    @Volatile
    private var instance: T? = null

    fun getInstance(): T {
        val result: T? = instance
        return result ?: synchronized(this) {
            val result2: T? = instance
            result2 ?: run {
                val created = _factory?.invoke()
                instance = created
                _factory = null
                created!!
            }
        }
    }
}
