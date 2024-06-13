package io.bluetape4k.okio

import okio.Buffer
import okio.BufferedSink
import okio.BufferedSource
import okio.Sink
import okio.Source
import okio.buffer

fun Buffer.asBufferedSource(): BufferedSource = (this as Source).buffered()

fun Buffer.asBufferedSink(): BufferedSink = (this as Sink).buffered()

fun Source.buffered(): BufferedSource = buffer()

fun Sink.buffered(): BufferedSink = buffer()
