package io.bluetape4k.hyperscan.block

import io.bluetape4k.logging.KLogging
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class HarmfulDomainFilterTest {

    companion object: KLogging()

    private val harmfulDomainFilter = HarmfulDomainFilter()

    @Test
    fun `domain 추출 테스트`() {
        val sentence = "너는 https://hog.tv에 방문하면 안돼"
        val domainRegex = "(http[s]?://)?([a-zA-Z0-9.-]+)".toRegex()
        val matchResult = domainRegex.find(sentence)
        val domain = matchResult?.groups?.get(2)?.value

        println(domain) // 출력 결과: hog.tv
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "너는 hog.tv에 가면 안돼",
            "웹사이트 kr.123rf.com를 소개합니다",
            "웹사이트 https://kr.123rf.com/를 소개합니다",
            "웹사이트 kr.123rf.com/path1/path2를 소개합니다"
        ]
    )
    fun `유해 사이트가 포함된 문자열인 경우`(text: String) {
        harmfulDomainFilter.contains(text).shouldBeTrue()
        harmfulDomainFilter.filter(text).shouldNotBeNull()
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "너는 google.com에 가봐",
            "너는 https://google.com에 가봐",
            "너는 https://google.com/path1/path2에 가봐",
            "너는 http://naver.com에서 찾아봐",
            "네이버에서 찾아봐",
        ]
    )
    fun `유해 사이트가 아닌 Domain이 포함된 문자열인 경우`(text: String) {
        harmfulDomainFilter.contains(text).shouldBeFalse()
        harmfulDomainFilter.filter(text).shouldBeNull()
    }
}
