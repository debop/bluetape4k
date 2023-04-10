package io.bluetape4k.junit5.folder

import org.junit.jupiter.api.extension.ExtendWith

/**
 * 테스트 시 임시폴더를 사용할 수 있도록 합니다.
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FILE,
    AnnotationTarget.FUNCTION
)
@MustBeDocumented
@Repeatable
@ExtendWith(TempFolderExtension::class)
annotation class TempFolderTest
