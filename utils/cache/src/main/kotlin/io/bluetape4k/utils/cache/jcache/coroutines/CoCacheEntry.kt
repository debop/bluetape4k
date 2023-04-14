package io.bluetape4k.utils.cache.jcache.coroutines

import java.io.Serializable
import javax.cache.Cache

data class CoCacheEntry<K: Any, V: Any>(
    private val key: K,
    private val value: V,
): Cache.Entry<K, V>, Serializable {

    override fun getKey(): K = key
    override fun getValue(): V = value

    override fun <T: Any> unwrap(clazz: Class<T>): T? = when {
        clazz.isAssignableFrom(javaClass) -> clazz.cast(this)
        else -> null
    }
}
