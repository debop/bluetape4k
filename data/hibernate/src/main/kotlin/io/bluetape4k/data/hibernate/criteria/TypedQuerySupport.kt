@file:Suppress("PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package io.bluetape4k.data.hibernate.criteria

import java.util.stream.Stream
import javax.persistence.NoResultException
import javax.persistence.TypedQuery

fun TypedQuery<java.lang.Long>.longList(): List<Long> = resultList.map { it.toLong() }
fun TypedQuery<java.lang.Long>.longStream(): Stream<Long> = resultStream.map { it.toLong() }
fun TypedQuery<java.lang.Long>.longResult(): Long? = findOneOrNull()?.toLong()

fun <T: Any> TypedQuery<T>.findOneOrNull(): T? = try {
    singleResult
} catch (e: NoResultException) {
    null
}
