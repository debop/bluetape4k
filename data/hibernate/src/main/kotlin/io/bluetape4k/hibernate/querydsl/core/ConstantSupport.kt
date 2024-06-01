package io.bluetape4k.hibernate.querydsl.core

import com.querydsl.core.types.Constant
import com.querydsl.core.types.ConstantImpl

fun constantOf(b: Boolean): Constant<Boolean> = ConstantImpl.create(b)
fun constantOf(c: Char): Constant<Char> = ConstantImpl.create(c)
fun constantOf(i: Byte): Constant<Byte> = ConstantImpl.create(i)
fun constantOf(i: Int): Constant<Int> = ConstantImpl.create(i)
fun constantOf(i: Long): Constant<Long> = ConstantImpl.create(i)
fun constantOf(i: Short): Constant<Short> = ConstantImpl.create(i)

inline fun <reified T> constantOf(constant: T): Constant<T> = ConstantImpl.create(T::class.java, constant)
