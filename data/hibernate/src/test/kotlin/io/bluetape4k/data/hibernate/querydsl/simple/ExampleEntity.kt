package io.bluetape4k.data.hibernate.querydsl.simple

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.core.requireNotEmpty
import io.bluetape4k.data.hibernate.model.IntJpaTreeEntity
import javax.persistence.*
import javax.validation.constraints.NotBlank

@Entity(name = "querydsl_example_entity")
@Access(AccessType.FIELD)
class ExampleEntity private constructor() : IntJpaTreeEntity<ExampleEntity>() {

    companion object {
        @JvmStatic
        operator fun invoke(name: String): ExampleEntity {
            name.requireNotEmpty("name")
            return ExampleEntity().apply {
                this.name = name
            }
        }
    }

    @NotBlank
    var name: String = ""
        protected set

    override fun equalProperties(other: Any): Boolean {
        return other is ExampleEntity && name == other.name
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: name.hashCode()
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}
