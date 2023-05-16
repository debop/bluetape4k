package io.bluetape4k.quarkus.kotlin.cdi

import javax.enterprise.inject.Instance
import javax.enterprise.inject.spi.CDI

inline fun <reified T: Any> CDI<Any>.selectAs(vararg qualifiers: Annotation): Instance<T> =
    select(T::class.java, *qualifiers)
