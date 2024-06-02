package io.bluetape4k.aws.ses.model

import software.amazon.awssdk.services.ses.model.Destination

inline fun destination(initializer: Destination.Builder.() -> Unit): Destination {
    return Destination.builder().apply(initializer).build()
}

fun destinationOf(
    toAddresses: Collection<String>,
    ccAddresses: Collection<String>? = null,
    bccAddresses: Collection<String>? = null,
) = destination {
    toAddresses(toAddresses)
    ccAddresses(ccAddresses)
    bccAddresses(bccAddresses)
}

fun destinationOf(vararg address: String): Destination = destination {
    toAddresses(*address)
}
