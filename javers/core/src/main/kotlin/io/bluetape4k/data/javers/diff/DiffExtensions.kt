package io.bluetape4k.data.javers.diff

import org.javers.core.diff.Change
import org.javers.core.diff.Diff

inline fun <reified T: Change> Diff.getObjectsByChangeType(): MutableList<Any?> =
    getObjectsByChangeType(T::class.java)

inline fun <reified T: Change> Diff.getChangesByType(): MutableList<T> =
    getChangesByType(T::class.java)
