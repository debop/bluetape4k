package io.bluetape4k.openai.client.model.core

import java.io.Serializable

@JvmInline
value class Status(val value: String): Serializable {
    companion object {
        val Succeeded: Status = Status("succeeded")
        val Processed: Status = Status("processed")
        val Deleted: Status = Status("deleted")
        val Failed: Status = Status("failed")
        val Cancelled: Status = Status("cancelled")
    }
}
