//package io.bluetape4k.hibernate.querydsl.simple
//
//import io.bluetape4k.core.ToStringBuilder
//import io.bluetape4k.hibernate.model.LongJpaTreeEntity
//import io.bluetape4k.support.requireNotEmpty
//import jakarta.persistence.Access
//import jakarta.persistence.AccessType
//import jakarta.persistence.Entity
//import jakarta.validation.constraints.NotBlank
//
//@Entity(name = "querydsl_example_entity")
//@Access(AccessType.FIELD)
//class ExampleEntity private constructor(): LongJpaTreeEntity<ExampleEntity>() {
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
