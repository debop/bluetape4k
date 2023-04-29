package io.bluetape4k.spring.cassandra.convert.converter

import java.util.Currency
import java.util.Locale
import org.springframework.core.convert.converter.Converter

enum class CurrencyConverter: Converter<Currency, String> {

    INSTANCE;

    override fun convert(source: Currency): String? {
        return source.getDisplayName(Locale.KOREA)
    }
}
