package io.bluetape4k.csv

import java.io.Closeable

/**
 * CSV 포맷 형태의 파일 정보를 [Record] 로 읽어드리는 Reader 입니다.
 */
interface RecordWriter: Closeable {

    fun writeHeaders(headers: Iterable<String>)

    fun writeHeaders(vararg headers: String) {
        writeHeaders(headers.toList())
    }

    fun writeRow(rows: Iterable<*>)

    /**
     * 하나의 엔티티를 여러 컬럼의 정보로 매핑하여 하나의 Record로 저장합니다.
     * @param entity T
     * @param mapper Function1<T, Iterable<*>>
     */
    fun <T> writeRow(entity: T, mapper: (T) -> Iterable<*>) {
        writeRow(mapper(entity))
    }

    /**
     * 복수개의 정보를 저장소에 씁니다.
     * @param rows 저장할 레코드들
     */
    fun writeAll(rows: Sequence<Iterable<*>>)

    fun <T> writeAll(entities: Sequence<T>, mapper: (T) -> Iterable<*>) {
        writeAll(entities.map(mapper))
    }
}
