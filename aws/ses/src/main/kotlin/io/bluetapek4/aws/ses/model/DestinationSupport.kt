package io.bluetapek4.aws.ses.model

import software.amazon.awssdk.services.ses.model.Destination

inline fun Destination(initializer: Destination.Builder.() -> Unit): Destination {
    return Destination.builder().apply(initializer).build()
}

fun destinationOf(
    toAddresses: Collection<String>,
    ccAddresses: Collection<String>? = null,
    bccAddresses: Collection<String>? = null,
) = Destination {
    toAddresses(toAddresses)
    ccAddresses(ccAddresses)
    bccAddresses(bccAddresses)
}

fun destinationOf(vararg address: String): Destination = Destination {
    toAddresses(*address)
}
