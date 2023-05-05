package io.bluetape4k.infra.graphql

import org.slf4j.MDC

fun getCopyOfLoggingContextMapOrEmpty(): Map<String, String> = MDC.getCopyOfContextMap() ?: emptyMap()
