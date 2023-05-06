package io.bluetape4k.aws.dynamodb.model

import software.amazon.awssdk.services.dynamodb.model.Projection
import software.amazon.awssdk.services.dynamodb.model.ProjectionType

inline fun projection(initializer: Projection.Builder.() -> Unit): Projection {
    return Projection.builder().apply(initializer).build()
}

fun projectionOf(
    projectionType: ProjectionType,
    nonKeyAttrs: Collection<String>? = null,
): Projection {
    return projection {
        projectionType(projectionType)
        nonKeyAttributes(nonKeyAttrs)
    }
}

fun projectionOf(
    projectionType: String,
    nonKeyAttrs: Collection<String>? = null,
): Projection {
    return projection {
        projectionType(projectionType)
        nonKeyAttributes(nonKeyAttrs)
    }
}
