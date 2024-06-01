package io.bluetape4k.jdbc.sql

import java.io.InputStream
import java.io.Reader
import java.sql.Blob
import java.sql.Clob
import java.sql.NClob


interface ArgumentSetter<in T> {

    val setter: (Int, T) -> Unit

    operator fun set(columnIndex: Int, value: T) {
        setter(columnIndex, value)
    }
}

interface ArgumentSetter2<in T, in A> {

    val setter2: (Int, T, A) -> Unit

    operator fun set(columnIndex: Int, arg: A, value: T) {
        setter2(columnIndex, value, arg)
    }
}

open class DefaultArgumentSetter<in T>(override val setter: (Int, T) -> Unit): ArgumentSetter<T>

open class ArgumentWithLengthSetter<in T>(
    override val setter: (Int, T) -> Unit,
    override val setter2: (Int, T, Int) -> Unit,
    val setterWithLong: (Int, T, Long) -> Unit,
): ArgumentSetter<T>, ArgumentSetter2<T, Int> {

    operator fun set(columnIndex: Int, length: Long, value: T) {
        setterWithLong(columnIndex, value, length)
    }
}

abstract class AbstractBlobArgumentSetter<in R>(
    override val setter: (Int, R) -> Unit,
    override val setter2: (Int, R, Long) -> Unit,
): ArgumentSetter<R>, ArgumentSetter2<R, Long>

class BlobArgumentSetter(
    val blobSetter: (Int, Blob) -> Unit,
    setter: (Int, InputStream) -> Unit,
    setter2: (Int, InputStream, Long) -> Unit,
): AbstractBlobArgumentSetter<InputStream>(setter, setter2) {

    operator fun set(columnIndex: Int, blob: Blob) {
        blobSetter(columnIndex, blob)
    }
}

class ClobArgumentSetter(
    val clobSetter: (Int, Clob) -> Unit,
    setter: (Int, Reader) -> Unit,
    setter2: (Int, Reader, Long) -> Unit,
): AbstractBlobArgumentSetter<Reader>(setter, setter2) {

    operator fun set(columnIndex: Int, clob: Clob) {
        clobSetter(columnIndex, clob)
    }
}

class NClobArgumentSetter(
    val nClobSetter: (Int, NClob) -> Unit,
    setter: (Int, Reader) -> Unit,
    setter2: (Int, Reader, Long) -> Unit,
): AbstractBlobArgumentSetter<Reader>(setter, setter2) {

    operator fun set(columnIndex: Int, nClob: NClob) {
        nClobSetter(columnIndex, nClob)
    }
}

open class CombinedArgumentSetter<in T, in A>(
    override val setter: (Int, T) -> Unit,
    override val setter2: (Int, T, A) -> Unit,
): ArgumentSetter<T>, ArgumentSetter2<T, A>

class ObjectArgumentSetter(
    setter: (Int, Any) -> Unit,
    setter2: (Int, Any, Int) -> Unit,
    val setter3: (Int, Any, Int, Int) -> Unit,
): CombinedArgumentSetter<Any, Int>(setter, setter2) {

    operator fun set(columnIndex: Int, targetSqlType: Int, scaleOrLength: Int, value: Any) {
        setter3(columnIndex, value, targetSqlType, scaleOrLength)
    }
}
