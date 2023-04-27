package io.bluetape4k.spring.beans

import io.bluetape4k.logging.KotlinLogging
import io.bluetape4k.logging.warn
import org.springframework.beans.BeansException
import org.springframework.beans.factory.BeanFactory
import kotlin.reflect.KClass

private val log = KotlinLogging.logger { }

inline fun <reified T: Any> BeanFactory.get(): T = getBean(T::class.java)

@Suppress("UNCHECKED_CAST")
operator fun <T: Any> BeanFactory.get(name: String): T? = getBean(name) as? T

operator fun <T: Any> BeanFactory.get(requiredType: KClass<T>): T = getBean(requiredType.java)

operator fun <T: Any> BeanFactory.get(requiredType: Class<T>): T = getBean(requiredType)

operator fun <T: Any> BeanFactory.get(name: String, requiredType: Class<T>): T = getBean(name, requiredType)

operator fun BeanFactory.get(name: String, vararg args: Any?): Any? = when {
    args.isEmpty() -> get(name) as? Any
    else -> getBean(name, *args)
}

/**
 * 지정된 수형의 Bean을 찾습니다. 없으면 null 을 반환합니다.
 */
fun <T: Any> BeanFactory.findBean(requiredType: KClass<T>): T? =
    findBean(requiredType.java)

/**
 * 지정된 수형의 Bean을 찾습니다. 없으면 null 을 반환합니다.
 */
fun <T: Any> BeanFactory.findBean(requiredType: Class<T>): T? {
    return try {
        get(requiredType)
    } catch (e: BeansException) {
        log.warn(e) { "Fail to find bean. requiredType=$requiredType, return null." }
        null
    }
}

/**
 * 지정된 이름과 수형의 Bean을 찾습니다. 없으면 null 을 반환합니다.
 */
fun <T: Any> BeanFactory.findBean(name: String, requiredType: Class<T>): T? {
    return try {
        get(name, requiredType)
    } catch (e: BeansException) {
        log.warn(e) { "Fail to find bean. name=$name, requiredType=$requiredType, return null." }
        null
    }
}

fun <T: Any> BeanFactory.findBean(name: String, vararg args: Any?): Any? {
    return try {
        get(name, *args)
    } catch (e: BeansException) {
        log.warn(e) { "Fail to find bean. name=$name, args=$args, return null." }
        null
    }
}
