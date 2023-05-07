package io.bluetape4k.spring.jpa.stateless

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.data.hibernate.model.IntJpaEntity
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany


@Entity
@DynamicInsert
@DynamicUpdate
class StatelessEntity(val name: String): IntJpaEntity() {

    var firstname: String? = null
    var lastname: String? = null
    var age: Int? = null
    var street: String? = null
    var city: String? = null
    var zipcode: String? = null

    override fun equalProperties(other: Any): Boolean =
        other is StatelessEntity && name == other.name

    override fun equals(other: Any?): Boolean = other != null && super.equals(other)

    override fun hashCode(): Int = id?.hashCode() ?: name.hashCode()

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}

@Entity
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
class StatelessMaster(val name: String): IntJpaEntity() {

    @OneToMany(mappedBy = "master", cascade = [CascadeType.ALL], fetch = FetchType.EAGER, orphanRemoval = true)
    val details: MutableList<StatelessDetail> = arrayListOf()

    override fun equalProperties(other: Any): Boolean =
        other is StatelessMaster && name == other.name

    override fun equals(other: Any?): Boolean = other != null && super.equals(other)

    override fun hashCode(): Int = id?.hashCode() ?: name.hashCode()

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }

}

@Entity
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
class StatelessDetail(val name: String): IntJpaEntity() {

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "master_id")
    var master: StatelessMaster? = null

    override fun equalProperties(other: Any): Boolean =
        other is StatelessDetail && name == other.name

    override fun equals(other: Any?): Boolean = other != null && super.equals(other)

    override fun hashCode(): Int = id?.hashCode() ?: name.hashCode()

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}
