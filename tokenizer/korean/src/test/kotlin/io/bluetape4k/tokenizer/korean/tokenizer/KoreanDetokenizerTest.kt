package io.bluetape4k.tokenizer.korean.tokenizer

import io.bluetape4k.tokenizer.korean.TestBase
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanDetokenizer.detokenize
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeTrue
import org.junit.jupiter.api.Test

class KoreanDetokenizerTest: TestBase() {

    @Test
    fun `should detokenize the input text`() = runTest {

        var actual = detokenize(listOf("연세", "대학교", "보건", "대학원", "에", "오신", "것", "을", "환영", "합니다", "!"))
        var expected = "연세대학교 보건 대학원에 오신것을 환영합니다!"
        actual shouldBeEqualTo expected

        actual = detokenize(listOf("와", "!!!", "iPhone", "6+", "가", ",", "드디어", "나왔다", "!"))
        val expected1 = "와!!! iPhone 6+"
        val expected2 = "가, 드디어 나왔다!"

        actual.contains(expected1).shouldBeTrue()
        actual.contains(expected2).shouldBeTrue()

        actual = detokenize(listOf("뭐", "완벽", "하진", "않", "지만", "그럭저럭", "쓸", "만", "하군", "..."))
        expected = "뭐 완벽하진 않지만 그럭저럭 쓸 만하군..."
        actual shouldBeEqualTo expected
    }

    @Test
    fun `should detokenize the edge cases`() = runTest {

        detokenize(listOf("")) shouldBeEqualTo ""

        detokenize(emptyList()) shouldBeEqualTo ""

        detokenize(listOf("완벽")) shouldBeEqualTo "완벽"

        detokenize(listOf("이")) shouldBeEqualTo "이"

        detokenize(listOf("이", "제품을", "사용하겠습니다")) shouldBeEqualTo "이 제품을 사용하겠습니다"
    }
}
