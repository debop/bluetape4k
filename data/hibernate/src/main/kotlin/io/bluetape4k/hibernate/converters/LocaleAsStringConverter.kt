package io.bluetape4k.hibernate.converters

import jakarta.persistence.AttributeConverter
import java.util.*

class LocaleAsStringConverter: AttributeConverter<Locale?, String?> {

    override fun convertToDatabaseColumn(attribute: Locale?): String? {
        return attribute?.toString()
    }

    override fun convertToEntityAttribute(dbData: String?): Locale? {
        return dbData?.run { Locale.forLanguageTag(this) }
    }

}
