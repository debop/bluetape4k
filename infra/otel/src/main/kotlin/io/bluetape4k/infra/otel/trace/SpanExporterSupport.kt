package io.bluetape4k.infra.otel.trace

import io.opentelemetry.exporter.logging.LoggingSpanExporter
import io.opentelemetry.sdk.common.CompletableResultCode
import io.opentelemetry.sdk.logs.export.InMemoryLogRecordExporter
import io.opentelemetry.sdk.trace.data.SpanData
import io.opentelemetry.sdk.trace.export.SpanExporter


/**
 * [InMemoryLogRecordExporter]를 생성합니다.
 */
fun inMemorySpanExporterOf(): InMemoryLogRecordExporter = InMemoryLogRecordExporter.create()

/**
 * [LoggingSpanExporter]를 생성합니다.
 */
fun loggingSpanExporterOf(): LoggingSpanExporter = LoggingSpanExporter.create()

/**
 * Returns a [SpanExporter] which delegates all exports to the [exporters] in order.
 *
 * Can be used to export to multiple backends using the same [io.opentelemetry.sdk.trace.SpanProcessor] like a
 * [io.opentelemetry.sdk.trace.export.SimpleSpanProcessor] or a [io.opentelemetry.sdk.trace.export.BatchSpanProcessor].
 */
fun spanExportOf(vararg exporters: SpanExporter): SpanExporter =
    SpanExporter.composite(*exporters)

fun SpanExporter.export(vararg spanDatas: SpanData): CompletableResultCode {
    return export(spanDatas.asList())
}
