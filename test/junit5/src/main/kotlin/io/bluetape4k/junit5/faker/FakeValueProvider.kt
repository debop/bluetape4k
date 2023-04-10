package io.bluetape4k.junit5.faker

object FakeValueProvider {
    private const val ADDRESS = "address"

    object Address {
        const val StreetName = "$ADDRESS.streetName"
        const val StreetAddressNumber = "$ADDRESS.streetAddressNumber"
        const val StreetAddress = "$ADDRESS.streetAddress"
        const val SecondaryAddress = "$ADDRESS.secondaryAddress"
        const val ZipCode = "$ADDRESS.zipCode"
    }

    private const val AVIATION = "aviation"

    object Aviation {
        const val Aircraft = "$AVIATION.aircraft"
        const val Airport = "$AVIATION.airport"
        const val Metar = "$AVIATION.METAR"
    }


    private const val ANCIENT = "ancient"

    object Ancient {
        const val God = "$ANCIENT.god"
        const val Primordial = "$ANCIENT.primordial"
        const val Titan = "$ANCIENT.titan"
        const val Hero = "$ANCIENT.hero"
    }

    private const val NAME = "name"

    object Name {
        const val FullName = "$NAME.fullName"
        const val FirstName = "$NAME.firstName"
        const val LastName = "$NAME.lastName"
        const val Username = "$NAME.username"
        const val Title = "$NAME.title"
    }
}
