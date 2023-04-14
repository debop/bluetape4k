package io.bluetape4k.tokenizer.model

/**
 * 금칙어의 심각도
 */
enum class Severity {
    LOW,
    MIDDLE,
    HIGH;

    companion object {
        val DEFAULT = Severity.LOW
    }
}
