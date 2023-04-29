package io.bluetape4k.data.hibernate.mapping.associations.onetoone

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.data.hibernate.AbstractHibernateTest
import io.bluetape4k.data.hibernate.model.IntJpaEntity
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.CascadeType
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.persistence.PrimaryKeyJoinColumn
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.jpa.repository.JpaRepository

class UnidirectionalOneToOneTest(
    @Autowired private val carRepo: CarRepository,
    @Autowired private val wheelRepo: WheelRepository
): AbstractHibernateTest() {

    @Test
    fun `unidirectional one to one with @MapsId`() {
        val car = Car("BMW")
        val wheel = Wheel("18-inch").apply { diameter = 18.0 }
        wheel.car = car

        // cascade가 PERSIST이므로, car 도 저장된다.
        val loaded = tem.persistFlushFind(wheel)

        loaded shouldBeEqualTo wheel
        loaded.car shouldBeEqualTo car

        flushAndClear()

        wheelRepo.delete(loaded)
        carRepo.existsById(car.id!!).shouldBeTrue()
        carRepo.delete(loaded.car!!)

        flushAndClear()

        carRepo.existsById(car.id!!).shouldBeFalse()
        wheelRepo.existsById(wheel.id!!).shouldBeFalse()
    }
}

@Entity(name = "onetoone_car")
@Access(AccessType.FIELD)
class Car(val brand: String): IntJpaEntity() {

    override fun equalProperties(other: Any): Boolean {
        return other is Car && brand == other.brand
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: brand.hashCode()
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("brand", brand)
    }
}

@Entity(name = "onetoone_wheel")
@Access(AccessType.FIELD)
class Wheel(val name: String): IntJpaEntity() {

    @Id
    override var id: Int? = null

    @MapsId
    @PrimaryKeyJoinColumn(name = "car_id")
    @OneToOne(cascade = [CascadeType.PERSIST], fetch = FetchType.LAZY)
    var car: Car? = null

    var diameter: Double? = null

    override fun hashCode(): Int {
        return id?.hashCode() ?: name.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun equalProperties(other: Any): Boolean {
        return other is Wheel && name == other.name
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("name", name)
    }
}

interface CarRepository: JpaRepository<Car, Int>

interface WheelRepository: JpaRepository<Wheel, Int>
