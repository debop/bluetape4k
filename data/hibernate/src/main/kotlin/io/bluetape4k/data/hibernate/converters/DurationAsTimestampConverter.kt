package io.bluetape4k.data.hibernate.converters

import java.sql.Timestamp
import java.time.Duration
import jakarta.persistence.AttributeConverter

/**
 * Java Time [Duration]을 [Timestamp]로 변환해서 저장하는 Converter
 *
 * ```
 *
 * @Convert(converter=DurationAsTimestampConverter::class)
 * var duration:Duration? = null
 * ```
 */
class DurationAsTimestampConverter: AttributeConverter<Duration?, Timestamp?> {

    override fun convertToDatabaseColumn(attribute: Duration?): Timestamp? {
        return attribute?.run { Timestamp(this.toMillis()) }
    }

    override fun convertToEntityAttribute(dbData: Timestamp?): Duration? {
        return dbData?.run { Duration.ofMillis(this.time) }
    }
}
