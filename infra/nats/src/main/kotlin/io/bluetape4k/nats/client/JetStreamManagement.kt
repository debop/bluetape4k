package io.bluetape4k.nats.client

import io.nats.client.JetStreamApiException
import io.nats.client.JetStreamManagement
import io.nats.client.api.StreamConfiguration

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
