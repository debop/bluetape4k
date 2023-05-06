package io.bluetape4k.data.hibernate.converter

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.data.hibernate.converters.DurationAsTimestampConverter
import io.bluetape4k.data.hibernate.converters.LZ4KryoObjectAsByteArrayConverter
import io.bluetape4k.data.hibernate.converters.LocaleAsStringConverter
import io.bluetape4k.data.hibernate.converters.RC2StringConverter
import io.bluetape4k.data.hibernate.model.IntJpaEntity
import io.bluetape4k.io.cryptography.randomBytes
import java.io.Serializable
import java.time.Duration
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity(name = "convertable_entity")
@Access(AccessType.FIELD)
class ConvertableEntity(
    @NotBlank
    val name: String,
): IntJpaEntity() {

    @Convert(converter = LocaleAsStringConverter::class)
    var locale: Locale = Locale.KOREA

    @Convert(converter = DurationAsTimestampConverter::class)
    var duration: Duration? = null

    @Convert(converter = RC2StringConverter::class)
    var password: String? = null

    @Convert(converter = LZ4KryoObjectAsByteArrayConverter::class)
    @Basic(fetch = FetchType.LAZY)
    val component: Component = Component("test data")

    override fun equalProperties(other: Any): Boolean {
        return other is ConvertableEntity && name == other.name
    }

    override fun equals(other: Any?): Boolean {
        return other != null && super.equals(other)
    }

    override fun hashCode(): Int = id?.hashCode() ?: name.hashCode()

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }

    data class Component(
        val name: String,
    ): Serializable {
        var largeText: ByteArray = randomBytes(512)
    }
}
