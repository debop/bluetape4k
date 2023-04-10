package io.bluetape4k.core.support

import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

class KoreanSupportTest {

    companion object : KLogging()

    @Test
    fun `초성 검출`() {
        val a = 'ㄱ'.code
        log.debug { "ㄱ = ${"%04X".format(a)}" }
    }

    @Test
    fun `문자열에 한글이 포함되었는가`() {
        "".containKorean().shouldBeFalse()
        "abc".containKorean().shouldBeFalse()

        "abcㄱㄴㄷ".containKorean().shouldBeTrue()
        "abcㅏㅜ".containKorean().shouldBeTrue()

        "abc가나다".containKorean().shouldBeTrue()
        "가나다abc".containKorean().shouldBeTrue()
        "가a나b다c".containKorean().shouldBeTrue()
    }

    @Test
    fun `한글을 자소단위로 분해한다`() {

        "한국".getJasoLetter() shouldBeEqualTo "ㅎㅏㄴㄱㅜㄱ"
        "Great 한국".getJasoLetter() shouldBeEqualTo "ㅎㅏㄴㄱㅜㄱ"

        "".getJasoLetter().shouldBeEmpty()
        " \t ".getJasoLetter().shouldBeEmpty()


    }

    @Test
    fun `한글의 초성만을 추출한다`() {

        "대한민국".getChosung() shouldBeEqualTo charArrayOf('ㄷ', 'ㅎ', 'ㅁ', 'ㄱ')
        "배성혁".getChosung() shouldBeEqualTo charArrayOf('ㅂ', 'ㅅ', 'ㅎ')

        "Great 한국".getChosung() shouldBeEqualTo charArrayOf('ㅎ', 'ㄱ')

        "".getChosung().shouldBeEmpty()
        " \t ".getChosung().shouldBeEmpty()
    }

}
