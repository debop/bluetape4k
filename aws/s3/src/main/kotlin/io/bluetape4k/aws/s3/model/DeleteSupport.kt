package io.bluetape4k.aws.s3.model

import software.amazon.awssdk.services.s3.model.Delete
import software.amazon.awssdk.services.s3.model.ObjectIdentifier

inline fun delete(initializer: Delete.Builder.() -> Unit): Delete {
    return Delete.builder().apply(initializer).build()
}

fun deleteOf(vararg objectIds: ObjectIdentifier, quiet: Boolean = false): Delete {
    return delete {
        objects(objectIds.toList())
        quiet(quiet)
    }
}

fun deleteOf(objectIds: Collection<ObjectIdentifier>, quiet: Boolean = false): Delete {
    return delete {
        objects(objectIds)
        quiet(quiet)
    }
}
