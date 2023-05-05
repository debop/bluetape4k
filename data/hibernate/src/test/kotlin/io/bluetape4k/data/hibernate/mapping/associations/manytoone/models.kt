package io.bluetape4k.data.hibernate.mapping.associations.manytoone

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.data.hibernate.model.IntJpaEntity
import org.hibernate.annotations.LazyToOne
import org.hibernate.annotations.LazyToOneOption
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.CascadeType.*
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany


@Entity(name = "manytoone_bear")
@Access(AccessType.FIELD)
class Beer(val name: String): IntJpaEntity() {

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = [MERGE, PERSIST, REFRESH])
    @JoinColumn(name = "brewery_id", nullable = false)
    @LazyToOne(LazyToOneOption.PROXY)
    var brewery: Brewery? = null

    override fun equalProperties(other: Any): Boolean {
        return other is Beer && name == other.name
    }

    override fun equals(other: Any?): Boolean {
        return other != null && super.equals(other)
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: name.hashCode()
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}

@Entity(name = "manytoone_brewery")
@Access(AccessType.FIELD)
class Brewery(val name: String): IntJpaEntity() {

    @OneToMany(mappedBy = "brewery", cascade = [ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val beers: MutableSet<Beer> = mutableSetOf()

    fun addBeers(vararg beersToAdd: Beer) {
        beersToAdd.forEach {
            if (this.beers.add(it)) {
                it.brewery = this
            }
        }
    }

    fun removeBeers(vararg beersToRemove: Beer) {
        beersToRemove.forEach {
            if (this.beers.remove(it)) {
                it.brewery = null
            }
        }
    }

    override fun equalProperties(other: Any): Boolean {
        return other is Brewery && name == other.name
    }

    override fun equals(other: Any?): Boolean {
        return other != null && super.equals(other)
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: name.hashCode()
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}

@Entity(name = "manytoone_jug")
@Access(AccessType.FIELD)
class Jug(val name: String): IntJpaEntity() {

    override fun equals(other: Any?): Boolean {
        return other != null && super.equals(other)
    }

    override fun hashCode(): Int = id?.hashCode() ?: name.hashCode()

    override fun equalProperties(other: Any): Boolean {
        return other is Jug && name == other.name
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}

@Entity(name = "manytoone_jugmeter")
@Access(AccessType.FIELD)
class JugMeter(val name: String): IntJpaEntity() {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "jug_id", nullable = false)
    var memberOf: Jug? = null

    override fun equalProperties(other: Any): Boolean {
        return other is JugMeter && name == other.name
    }

    override fun equals(other: Any?): Boolean {
        return other != null && super.equals(other)
    }

    override fun hashCode(): Int = id?.hashCode() ?: name.hashCode()

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}

@Entity(name = "manytoone_salesguy")
@Access(AccessType.FIELD)
class SalesGuy(val name: String): IntJpaEntity() {

    @ManyToOne(fetch = FetchType.LAZY, cascade = [MERGE, PERSIST, REFRESH])
    @JoinColumn(name = "sales_force_id")
    @LazyToOne(LazyToOneOption.PROXY)
    var salesForce: SalesForce? = null

    override fun equalProperties(other: Any): Boolean {
        return other is SalesGuy && name == other.name
    }

    override fun equals(other: Any?): Boolean {
        return other != null && super.equals(other)
    }

    override fun hashCode(): Int = id?.hashCode() ?: name.hashCode()

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}

@Entity(name = "manytoone_salesforce")
@Access(AccessType.FIELD)
class SalesForce(val name: String): IntJpaEntity() {

    @OneToMany(mappedBy = "salesForce", cascade = [ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val salesGuys: MutableSet<SalesGuy> = mutableSetOf()

    fun addGuys(vararg guysToAdd: SalesGuy) {
        guysToAdd.forEach {
            if (this.salesGuys.add(it)) {
                it.salesForce = this
            }
        }
    }

    fun removeGuys(vararg guysToRemove: SalesGuy) {
        guysToRemove.forEach {
            if (this.salesGuys.remove(it)) {
                it.salesForce = null
            }
        }
    }

    override fun equalProperties(other: Any): Boolean {
        return other is SalesForce && name == other.name
    }

    override fun equals(other: Any?): Boolean {
        return other != null && super.equals(other)
    }

    override fun hashCode(): Int = id?.hashCode() ?: name.hashCode()

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}
