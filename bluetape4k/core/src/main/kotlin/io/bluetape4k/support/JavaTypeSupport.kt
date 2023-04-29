package io.bluetape4k.support

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

fun Type.actualIteratorTypeArgument(): Type {
    val self = this
    if (self !is ParameterizedType) {
        throw IllegalArgumentException("Not supported type $self")
    }
    if (self.rawType != Iterator::class.java) {
        throw IllegalArgumentException("Not an iterator type ${self.rawType}")
    }
    return self.actualTypeArguments[0]
}
