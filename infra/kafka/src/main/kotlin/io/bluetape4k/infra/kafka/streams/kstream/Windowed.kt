package io.bluetape4k.infra.kafka.streams.kstream

import org.apache.kafka.streams.kstream.Window
import org.apache.kafka.streams.kstream.Windowed

fun <K> windowedOf(key: K, window: Window): Windowed<K> = Windowed(key, window)
