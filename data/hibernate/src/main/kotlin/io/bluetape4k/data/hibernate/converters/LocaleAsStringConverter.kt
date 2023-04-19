package io.bluetape4k.data.hibernate.converters

import jakarta.persistence.AttributeConverter
import java.util.Locale

class LocaleAsStringConverter: AttributeConverter<Locale?, String?> {

    override fun convertToDatabaseColumn(attribute: Locale?): String? {
        return attribute?.toString()
    }

    override fun convertToEntityAttribute(dbData: String?): Locale? {
        return dbData?.run { Locale.forLanguageTag(this) }
    }

}
