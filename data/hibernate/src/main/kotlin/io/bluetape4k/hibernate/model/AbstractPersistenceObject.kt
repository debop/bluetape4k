package io.bluetape4k.hibernate.model

import io.bluetape4k.core.AbstractValueObject
import jakarta.persistence.Transient

abstract class AbstractPersistenceObject: AbstractValueObject(), PersistenceObject {

    @get:Transient
    override val isPersisted: Boolean = false

}
