package io.bluetape4k.data.hibernate.querydsl.simple

// FIXME: Kapt 작업에서 예외가 발생한다.
//import io.bluetape4k.core.ToStringBuilder
//import io.bluetape4k.core.requireNotEmpty
//import io.bluetape4k.data.hibernate.model.LongJpaTreeEntity
//import jakarta.persistence.Access
//import jakarta.persistence.AccessType
//import jakarta.persistence.Entity
//import jakarta.validation.constraints.NotBlank

//@Entity(name = "querydsl_example_entity")
//@Access(AccessType.FIELD)
//class ExampleEntity private constructor(): LongJpaTreeEntity() {
//
//    companion object {
//        @JvmStatic
//        operator fun invoke(name: String): ExampleEntity {
//            name.requireNotEmpty("name")
//            return ExampleEntity().apply {
//                this.name = name
//            }
//        }
//    }
//
//    @NotBlank
//    var name: String = ""
//        protected set
//
//    override fun equalProperties(other: Any): Boolean {
//        return other is ExampleEntity && name == other.name
//    }
//
//    override fun equals(other: Any?): Boolean {
//        return other != null && super.equals(other)
//    }
//
//    override fun hashCode(): Int {
//        return id?.hashCode() ?: name.hashCode()
//    }
//
//    override fun buildStringHelper(): ToStringBuilder {
//        return super.buildStringHelper()
//            .add("name", name)
//    }
//}
