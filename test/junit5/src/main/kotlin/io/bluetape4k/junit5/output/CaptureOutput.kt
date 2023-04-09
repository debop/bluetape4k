package io.bluetape4k.junit5.output

import org.junit.jupiter.api.extension.ExtendWith

/**
 * 테스트 시의 [System.out]이나 [System.err]에 출력되는 정보를 Capture하여, 테스트를 검사할 수 있도록 합니다.
 *
 * ```kotlin
 * @CaptureOutput
 * class TestClass {
 *     fun testOutput(output: OutputCapturer) {
 *         println("Print to System.out!")
 *
 *         output.expect { "System.out!" }
 *         output.capture() shouldContain "System.out!"
 *     }
 * }
 * ```
 *
 * 테스트 메소드에 직접 적용
 * ```kotlin
 * @CaptureOutput
 * fun testOutput(output: OutputCapturer) {
 *     System.err.println("Print to System.err!")
 *
 *     output.expect { "System.err!" }
 *     output.capture() shouldContain "System.err!"
 * }
 * ```
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION
)
@MustBeDocumented
@Repeatable
@ExtendWith(CaptureOutputExtension::class)
annotation class CaptureOutput
