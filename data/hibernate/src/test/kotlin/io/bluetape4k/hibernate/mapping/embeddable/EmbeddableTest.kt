package io.bluetape4k.hibernate.mapping.embeddable

import io.bluetape4k.hibernate.AbstractHibernateTest
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test

class EmbeddableTest: AbstractHibernateTest() {

    private fun newPerson(): EmbeddablePerson {
        return EmbeddablePerson(
            faker.internet().username(),
            faker.internet().password()
        ).apply {
            email = faker.internet().emailAddress()
            homeAddress = EmbeddableAddress(
                faker.address().streetAddress(),
                faker.address().city(),
                faker.address().zipCode()
            )
            officeAddress = EmbeddableAddress(
                faker.address().streetAddress(),
                faker.address().city(),
                faker.address().zipCode()
            )
        }
    }

    @Test
    fun `entity has multiple embeddable`() {
        val person = newPerson()

        val loaded = tem.persistFlushFind(person)

        loaded shouldBeEqualTo person
        loaded.homeAddress shouldBeEqualTo person.homeAddress
        loaded.officeAddress shouldBeEqualTo person.officeAddress

        tem.remove(loaded)
        flushAndClear()

        tem.find(EmbeddablePerson::class.java, person.id) shouldBeEqualTo null
    }
}
