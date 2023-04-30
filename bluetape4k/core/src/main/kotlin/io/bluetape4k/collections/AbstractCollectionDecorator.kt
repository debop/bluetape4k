package io.bluetape4k.collections

import java.io.Serializable

abstract class AbstractCollectionDecorator<E>(
    protected val decorated: MutableCollection<E>,
): MutableCollection<E> by decorated, Serializable
