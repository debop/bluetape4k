package io.bluetape4k.nats.client

import io.bluetape4k.nats.client.api.streamConfiguration
import io.nats.client.Connection
import io.nats.client.api.StorageType
import io.nats.client.api.StreamInfo

fun Connection.createStream(
    streamName: String,
    storageType: StorageType = StorageType.Memory,
    vararg subjects: String,
): StreamInfo {
    val jsm = jetStreamManagement()

    val sc = streamConfiguration {
        name(streamName)
        storageType(storageType)
        subjects(*subjects)
    }
    return jsm.addStream(sc)
}

fun Connection.createOrReplaceStream(
    streamName: String,
    storageType: StorageType = StorageType.Memory,
    vararg subjects: String,
): StreamInfo {
    val jsm = this.jetStreamManagement()
    runCatching { jsm.deleteStream(streamName) }
    // Create a stream
    return createStream(streamName, storageType, *subjects)
}

fun Connection.createStreamOrUpdateSubjects(
    streamName: String,
    storageType: StorageType = StorageType.Memory,
    vararg subjects: String,
): StreamInfo {
    val jsm = this.jetStreamManagement()

    val si = jsm.getStreamInfoOrNull(streamName)
        ?: return createStream(streamName, storageType, *subjects)

    val sc = si.configuration
    var needToUpdate = false
    subjects.forEach {
        if (!sc.subjects.contains(it)) {
            needToUpdate = true
            sc.subjects.add(it)
        }
    }
    return if (needToUpdate) {
        val updatedSc = streamConfiguration(sc) { subjects(sc.subjects) }
        jsm.updateStream(updatedSc)
    } else {
        si
    }
}
