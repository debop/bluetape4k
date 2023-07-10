package io.nats.examples.chainOfCommand

data class Input(
    val aId: Int = -1,
    val bId: Int = -1,
) {

    override fun toString(): String {
        return "Worker A$aId, Workder B$bId"
    }
}
