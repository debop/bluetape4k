package io.bluetape4k.data.hibernate.mapping.compositeid

import io.bluetape4k.core.ToStringBuilder
import io.bluetape4k.data.hibernate.model.AbstractPersistenceObject
import io.bluetape4k.support.hashOf
import java.io.Serializable
import javax.persistence.Access
import javax.persistence.AccessType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Id
import javax.persistence.IdClass


data class CarIdentifier(
    val brand: String = "",
    val carYear: Int = 0
): Serializable

@Entity
@IdClass(CarIdentifier::class)
@Access(AccessType.FIELD)
class IdClassCar private constructor(): AbstractPersistenceObject() {

    companion object {
        @JvmStatic
        operator fun invoke(carId: CarIdentifier): IdClassCar {
            return IdClassCar().apply {
                brand = carId.brand
                carYear = carId.carYear
            }
        }

        @JvmStatic
        operator fun invoke(brand: String, carYear: Int): IdClassCar {
            return IdClassCar().apply {
                this.brand = brand
                this.carYear = carYear
            }
        }
    }

    @Id
    @Column(name = "car_brand", nullable = false, length = 64)
    var brand: String = ""

    @Id
    @Column(name = "car_year", nullable = false)
    var carYear: Int = 0

    var serialNo: String? = null

    fun carIdentifier(): CarIdentifier = CarIdentifier(brand, carYear)

    override fun equalProperties(other: Any): Boolean {
        return other is IdClassCar && other.brand == brand && other.carYear == carYear
    }

    override fun hashCode(): Int {
        return hashOf(brand, carYear)
    }

    override fun buildStringHelper(): ToStringBuilder {
        return super.buildStringHelper()
            .add("brand", brand)
            .add("carYear", carYear)
            .add("serialNo", serialNo)
    }
}