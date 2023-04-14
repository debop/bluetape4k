package io.bluetape4k.junit5.faker

import net.datafaker.Faker
import net.datafaker.service.RandomService


/**
 * Java Faker를 이용한
 *
 * @constructor Create empty Fakers
 */
object Fakers {
    /**
     * Faker
     */
    val faker: Faker = Faker()

    /**
     * Fake 값을 제공하는 난수발생기
     */
    val random: RandomService = faker.random()

    /**
     * 임의의 길이의 fake 문자열을 생성합니다.
     *
     * @param minimumLength    최소크기
     * @param maximumLength    최대크기
     * @param includeUppercase 대문자 포함 여부
     * @return 임의의 길이의 fake 문자열
     */
    fun randomString(
        minimumLength: Int = 2,
        maximumLength: Int = 255,
        includeUppercase: Boolean = true,
    ): String =
        faker.lorem().characters(minimumLength, maximumLength, includeUppercase)

    /**
     * [length] 크기의 fake 문자열을 반환합니다.
     *
     * @param length 문자열 길이
     * @return [lengh] 길이의 fake 문자열
     */
    fun fixedString(length: Int): String = faker.lorem().characters(length)

    /**
     * [format]에 `#`을 임의의 숫자(0~9)로 치환하는 문자열을 빌드합니다.
     *
     * ```
     * val phone = numberString("010-####-####")   // "010-1234-5678"
     * ```
     *
     * @param format 원하는 문자열 포맷
     * @return 랜덤 수로 치환된 문자열
     */
    fun numberString(format: String): String = faker.numerify(format)

    /**
     * [format]에 `?`를 임의의 character(`a`~`z`)로 치환한 문자열을 빌드합니다.
     *
     * ```
     * val text = letterString("?-103")  // "A-103", "c-701"
     * ```
     *
     * @param format  원하는 문자열 포맷
     * @param isUpper 대문자로 할 것인가?
     * @return 랜덤 character로 치환된 문자열
     */
    fun letterString(format: String, isUpper: Boolean = false): String =
        faker.letterify(format, isUpper)

    /**
     * [numberString], [letterString] 을 조합하여, `#` 는 숫자로, `?` 는 문자로 치환한 문자열을 빌드합니다.
     *
     * ```
     * val text = alphaNumbericString("?-#00#")   // a-4007
     * ```
     *
     * @param format  원하는 문자열 포맷
     * @param isUpper 대문자로 할 것인가?
     * @return
     */
    fun alphaNumericString(format: String, isUpper: Boolean = false): String =
        faker.bothify(format, isUpper)

}