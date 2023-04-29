package io.bluetape4k.support

import kotlin.reflect.KClass


fun getClassLoader(clazz: Class<*>): ClassLoader {
//    return System.getSecurityManager()?.run {
//        AccessController.doPrivileged(PrivilegedAction { clazz.classLoader })
//    } ?: clazz.classLoader
    return clazz.classLoader
}

fun getClassLoader(kclass: KClass<*>): ClassLoader = getClassLoader(kclass.java)

inline fun <reified T> getClassLoader(): ClassLoader = getClassLoader(T::class.java)

fun getDefaultClassLoader(): ClassLoader = getContextClassLoader()

fun getContextClassLoader(): ClassLoader = getClassLoader { Thread.currentThread().contextClassLoader }

fun getSystemClassLoader(): ClassLoader = getClassLoader { ClassLoader.getSystemClassLoader() }

private inline fun getClassLoader(crossinline loader: () -> ClassLoader): ClassLoader {
    return runCatching { loader() }.getOrElse { Thread.currentThread().contextClassLoader }
}
