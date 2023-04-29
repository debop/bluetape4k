package io.bluetape4k.data.hibernate.mapping.associations.onetomany.list

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.data.hibernate.model.IntJpaEntity
import io.bluetape4k.support.hashOf
import java.time.LocalDate
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToOne
import javax.persistence.OneToMany
import javax.persistence.OrderBy
import javax.persistence.OrderColumn
import javax.validation.constraints.NotBlank
import org.hibernate.annotations.DynamicInsert
import org.hibernate.annotations.DynamicUpdate
import org.hibernate.annotations.LazyCollection
import org.hibernate.annotations.LazyCollectionOption


@Entity(name = "onetomany_father")
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
class Father(val name: String): IntJpaEntity() {

    // One To Many 를 Join Table을 이용하여 Unidirection으로 연결한다
    //
    @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinTable(
        name = "onetomany_father_children",
        joinColumns = [JoinColumn(name = "father_id")],
        inverseJoinColumns = [JoinColumn(name = "child_id")]
    )
    @OrderColumn(name = "birtyday", nullable = false) // JoinTable 사용 시에는 OrderColumn이 적용되지 않는다. 대신 @OrderBy를 사용해라
    @OrderBy("birthday")
    var orderedChildren: MutableList<Child> = mutableListOf()

    override fun equalProperties(other: Any): Boolean {
        return other is Father && name == other.name
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: name.hashCode()
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}

@Entity(name = "onetomany_child")
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
class Child(
    @NotBlank val name: String,
    val birthday: LocalDate
): IntJpaEntity() {

    override fun equalProperties(other: Any): Boolean {
        return other is Child && name == other.name && birthday == other.birthday
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: hashOf(name, birthday)
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
            .add("birthday", birthday)
    }
}

@Entity(name = "onetomany_order")
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
class Order(@NotBlank val no: String): IntJpaEntity() {

    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("name")
    @LazyCollection(LazyCollectionOption.EXTRA)
    val items: MutableList<OrderItem> = arrayListOf()

    fun addItems(vararg itemsToAdd: OrderItem) {
        itemsToAdd.forEach {
            if (items.add(it)) {
                it.order = this
            }
        }
    }

    fun removeItems(vararg itemsToRemove: OrderItem) {
        itemsToRemove.forEach {
            if (items.remove(it)) {
                it.order = null
            }
        }
    }

    override fun equalProperties(other: Any): Boolean {
        return other is Order && no == other.no
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: no.hashCode()
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("no", no)
    }
}

@Entity(name = "onetomany_order_item")
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
class OrderItem(val name: String): IntJpaEntity() {

    @ManyToOne
    @JoinColumn(name = "order_id")
    var order: Order? = null

    override fun equalProperties(other: Any): Boolean {
        return other is OrderItem && name == other.name
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: name.hashCode()
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}

@Entity(name = "onetomany_batch")
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
class Batch(
    val name: String
): IntJpaEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    override var id: Int? = null

    // One To Many 를 Join Table을 이용하여 Unidirection으로 연결한다
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    val items: MutableList<BatchItem> = arrayListOf()

    fun addItems(vararg itemsToAdd: BatchItem) {
        itemsToAdd.forEach {
            if (items.add(it)) {
                it.batch = this
            }
        }
    }

    fun removeItems(vararg itemsToRemove: BatchItem) {
        itemsToRemove.forEach {
            if (items.remove(it)) {
                it.batch = null
            }
        }
    }

    override fun equalProperties(other: Any): Boolean {
        return other is Batch && name == other.name
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: name.hashCode()
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}

@Entity(name = "onetomany_batch_item")
@Access(AccessType.FIELD)
@DynamicInsert
@DynamicUpdate
class BatchItem(
    val name: String
): IntJpaEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_item_id")
    override var id: Int? = null

    @ManyToOne
    var batch: Batch? = null

    override fun equalProperties(other: Any): Boolean {
        return other is BatchItem && name == other.name
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: name.hashCode()
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}
