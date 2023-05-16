package io.bluetape4k.workshop.quarkus.model

import io.bluetape4k.core.AbstractValueObject
import io.bluetape4k.core.ToStringBuilder
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import java.util.*
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table


@Entity
@Table(name = "Fruit")
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
class Fruit internal constructor(): AbstractValueObject() {

    companion object {
        @JvmStatic
        operator fun invoke(name: String, description: String): Fruit {
            return Fruit().apply {
                this.name = name
                this.description = description
            }
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null

    @Column(nullable = false, unique = true, length = 40)
    var name: String = ""

    var description: String? = null

    override fun equalProperties(other: Any): Boolean {
        return other is Fruit && name == other.name
    }

    override fun equals(other: Any?): Boolean = other != null && super.equals(other)

    override fun hashCode(): Int = id?.hashCode() ?: name.hashCode()

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("id", id)
            .add("name", name)
    }
}

//@Entity
//@Table(name = "Fruit")
//@Access(AccessType.FIELD)
//class Fruit internal constructor(): java.io.Serializable {
//
//    companion object {
//        @JvmStatic
//        operator fun invoke(name: String, description: String): Fruit =
//            Fruit().apply {
//                this.name = name
//                this.description = description
//            }
//    }
//
//    @field:Id
//    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
//    val id: Long? = null
//
//    @field:Column(nullable = false, unique = true, length = 40)
//    var name: String = ""
//
//    var description: String? = null
//
//    override fun equals(other: Any?): Boolean {
//        return other is Fruit && id == other.id
//    }
//
//    override fun hashCode(): Int {
//        return id?.hashCode() ?: name.hashCode()
//    }
//
//    override fun toString(): String {
//        return StringJoiner(", ", Fruit::class.simpleName + "[", "]")
//            .add("id=$id")
//            .add("name='$name'")
//            .add("description='$description'")
//            .toString()
//    }
//}
