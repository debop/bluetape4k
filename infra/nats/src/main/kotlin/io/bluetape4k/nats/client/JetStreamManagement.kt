package io.bluetape4k.nats.client

import io.bluetape4k.nats.client.api.streamConfiguration
import io.nats.client.JetStreamApiException
import io.nats.client.JetStreamManagement
import io.nats.client.api.StorageType
import io.nats.client.api.StreamConfiguration
import io.nats.client.api.StreamInfo

fun JetStreamManagement.deleteStreamIfExists(streamName: String) {
    runCatching { deleteStream(streamName) }
}

fun JetStreamManagement.tryPurgeStream(
    streamName: String,
    streamConfigurationCreator: () -> StreamConfiguration = { StreamConfiguration.builder().name(streamName).build() },
) {
    try {
        purgeStream(streamName)
    } catch (je: JetStreamApiException) {
        if (je.apiErrorCode == 10059) {
            addStream(streamConfigurationCreator())
        } else {
            throw je
        }
    }
}

fun JetStreamManagement.getStreamInfoOrNull(streamName: String): StreamInfo? {
    try {
        return getStreamInfo(streamName)
    } catch (jsae: JetStreamApiException) {
        if (jsae.apiErrorCode == 10059) {
            return null
        }
        throw jsae
    }
}

fun JetStreamManagement.streamExists(streamName: String): Boolean =
    getStreamInfoOrNull(streamName) != null


fun JetStreamManagement.createStream(
    streamName: String,
    storageType: StorageType = StorageType.Memory,
    vararg subjects: String,
): StreamInfo {
    val sc = streamConfiguration {
        name(streamName)
        storageType(storageType)
        subjects(*subjects)
    }
    return addStream(sc)
}

fun JetStreamManagement.createOrReplaceStream(
    streamName: String,
    subject: String,
): StreamInfo = createOrReplaceStream(streamName, subjects = arrayOf(subject))


fun JetStreamManagement.createOrReplaceStream(
    streamName: String,
    storageType: StorageType = StorageType.Memory,
    vararg subjects: String,
): StreamInfo {
    runCatching { deleteStream(streamName) }
    // Create a stream
    return createStream(streamName, storageType, *subjects)
}

fun JetStreamManagement.createStreamOrUpdateSubjects(
    streamName: String,
    storageType: StorageType = StorageType.Memory,
    vararg subjects: String,
): StreamInfo {

    val si = getStreamInfoOrNull(streamName)
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
        updateStream(updatedSc)
    } else {
        si
    }
}
