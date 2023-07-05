package io.bluetape4k.data.hibernate.converters

import java.util.*
import jakarta.persistence.AttributeConverter

class LocaleAsStringConverter: AttributeConverter<Locale?, String?> {

    override fun convertToDatabaseColumn(attribute: Locale?): String? {
        return attribute?.toString()
    }

    override fun convertToEntityAttribute(dbData: String?): Locale? {
        return dbData?.run { Locale.forLanguageTag(this) }
    }

}
