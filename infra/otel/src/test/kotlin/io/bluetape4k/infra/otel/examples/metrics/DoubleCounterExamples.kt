package io.bluetape4k.infra.otel.examples.metrics

import io.bluetape4k.infra.otel.AbstractOtelTest
import io.bluetape4k.infra.otel.common.attributesOf
import io.bluetape4k.infra.otel.common.toAttributeKey
import io.bluetape4k.infra.otel.coroutines.useSpanAwait
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.api.metrics.DoubleCounter
import io.opentelemetry.api.trace.SpanKind
import io.opentelemetry.api.trace.StatusCode
import java.io.File
import java.nio.file.Path
import kotlinx.coroutines.delay
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

class DoubleCounterExamples: AbstractOtelTest() {

    companion object: KLogging() {
        private val homeDir: File = Path.of(".").toAbsolutePath().toFile()
        private val fileExtensionAttrKey = "file_extension".toAttributeKey()
    }

    @ParameterizedTest
    @MethodSource(JUNIT_METHOD_SOURCE_NAME)
    fun `measure file size in coroutines`(name: String, otel: OpenTelemetry) = runSuspendWithIO {
        log.debug { "Use $name OpenTelemetry in Coroutines" }

        val tracer = otel.getTracer("examples.metrics", "0.13.4")
        val meter = otel.getMeter("examples.metrics")

        val diskSpaceCounter = meter.counterBuilder("calculated_used_space")
            .setDescription("파일확장자별 사용된 디스크 공간")
            .setUnit("MB")
            .ofDoubles()
            .build()

        tracer.spanBuilder("calculate space")
            .setSpanKind(SpanKind.INTERNAL)
            .useSpanAwait { span ->
                try {
                    val extensionsToFind = listOf("jar", "kt", "java", "class", "xml", "properties", "txt")
                    calculateSpaceUsedByFilesWithExtensionsAsync(extensionsToFind, homeDir, diskSpaceCounter)
                } catch (e: Exception) {
                    span.recordException(e)
                    span.setStatus(StatusCode.ERROR, "Error while calculating used space")
                }
            }
        log.debug { diskSpaceCounter }
    }

    private suspend fun calculateSpaceUsedByFilesWithExtensionsAsync(
        extensions: List<String>,
        directory: File,
        diskSpaceCounter: DoubleCounter,
    ) {
        directory.listFiles()
            ?.forEach { file ->
                if (file.isDirectory) {
                    delay(100L)
                    calculateSpaceUsedByFilesWithExtensionsAsync(extensions, file, diskSpaceCounter)
                } else {
                    extensions.find { file.extension == it }?.let { ext ->
                        log.debug { "Update disk space. $file" }
                        diskSpaceCounter.add(file.length().toDouble(), attributesOf(fileExtensionAttrKey, ext))
                    }
                }
            }
    }
}
