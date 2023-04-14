package io.bluetape4k.tokenizer.korean.tokenizer

import io.bluetape4k.logging.KLogging
import io.bluetape4k.tokenizer.korean.TestBase
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanTokenizer.tokenize
import io.bluetape4k.tokenizer.korean.utils.KoreanDictionaryProvider
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Adjective
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Adverb
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Conjunction
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Determiner
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Eomi
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Exclamation
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Josa
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.KoreanParticle
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Noun
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Punctuation
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Space
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Suffix
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Verb
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.VerbPrefix
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeFalse
import org.amshove.kluent.shouldBeTrue
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Test


class KoreanTokenizerTest: TestBase() {

    companion object: KLogging() {
        private val HA = KoreanToken("하", Noun, 0, 0)
        private val HA_UNKNOWN = KoreanToken("하", Noun, 0, 0, unknown = true)
        private val HAHA_UNKNOWN = KoreanToken("하하", Noun, 0, 0, unknown = true)

        private val HUMAN = KoreanToken("사람", Noun, 0, 0)
        private val DOG = KoreanToken("강아지", Noun, 0, 0)

        private val HADA = KoreanToken("하다", Verb, 0, 0)
    }

    private val parsedChunk = ParsedChunk(listOf(HA, HA, HA), 1)
    private val parsedChunkWithTwoTokens = ParsedChunk(listOf(HA, HA), 1)
    private val parsedChunkWithUnknowns = ParsedChunk(listOf(HAHA_UNKNOWN, HA_UNKNOWN, HA), 1)
    private val parsedChunkWithCommonNouns = ParsedChunk(listOf(HUMAN, DOG), 1)
    private val parsedChunkWithVerbs = ParsedChunk(listOf(HUMAN, HADA), 1)
    private val parsedChunkWithExactMatch = ParsedChunk(listOf(DOG), 1)

    @Test
    fun `should count unknowns`() {
        parsedChunkWithUnknowns.countUnknowns shouldBeEqualTo 2
        parsedChunk.countUnknowns shouldBeEqualTo 0
    }

    @Test
    fun `should count tokens`() {
        parsedChunk.countTokens shouldBeEqualTo 3
        parsedChunkWithTwoTokens.countTokens shouldBeEqualTo 2
    }

    @Test
    fun `should get correct frequeency score`() {
        parsedChunkWithTwoTokens.getFreqScore() shouldBeEqualTo 1.0f
        parsedChunkWithCommonNouns.getFreqScore() shouldBeEqualTo 0.4544f
    }

    @Test
    fun `should count POSes`() {
        parsedChunk.countPos(Noun) shouldBeEqualTo 3
        parsedChunkWithVerbs.countPos(Noun) shouldBeEqualTo 1
        parsedChunkWithVerbs.countPos(Verb) shouldBeEqualTo 1
    }

    @Test
    fun `should determine if the chunk is an exact match`() {
        parsedChunk.isExactMatch shouldBeEqualTo 1
        parsedChunkWithExactMatch.isExactMatch shouldBeEqualTo 0
    }

    @Test
    fun `should determine if the chunk is all noun`() {
        parsedChunk.isAllNouns shouldBeEqualTo 0
        parsedChunkWithVerbs.isAllNouns shouldBeEqualTo 1
    }

    @Test
    fun `tokenize should return expected tokens`() {
        var actual = tokenize("개루루야")
        var expected = listOf(
            KoreanToken("개", Noun, 0, 1),
            KoreanToken("루루", Noun, 1, 2),
            KoreanToken("야", Josa, 3, 1)
        )
        actual shouldContainSame expected

        actual = tokenize("쵸귀여운")
        expected = listOf(
            KoreanToken("쵸", VerbPrefix, 0, 1),
            KoreanToken("귀여운", Adjective, 1, 3, stem = "귀엽다")
        )
        actual shouldContainSame expected

        actual = tokenize("이사람의")
        expected = listOf(
            KoreanToken("이", Determiner, 0, 1),
            KoreanToken("사람", Noun, 1, 2),
            KoreanToken("의", Josa, 3, 1)
        )
        actual shouldContainSame expected

        actual = tokenize("엄청작아서귀엽다")
        expected = listOf(
            KoreanToken("엄청", Adverb, 0, 2),
            KoreanToken("작아서", Adjective, 2, 3, stem = "작다"),
            KoreanToken("귀엽다", Adjective, 5, 3, stem = "귀엽다")
        )
        actual shouldContainSame expected

        actual = tokenize("안녕하셨어요")
        expected = listOf(KoreanToken("안녕하셨어요", Adjective, 0, 6, stem = "안녕하다"))
        actual shouldContainSame expected

        actual = tokenize("쵸귀여운개루루")
        expected = listOf(
            KoreanToken("쵸", VerbPrefix, 0, 1),
            KoreanToken("귀여운", Adjective, 1, 3, stem = "귀엽다"),
            KoreanToken("개", Noun, 4, 1),
            KoreanToken("루루", Noun, 5, 2)
        )
        actual shouldContainSame expected

        actual = tokenize("그리고")
        expected = listOf(KoreanToken("그리고", Conjunction, 0, 3))
        actual shouldContainSame expected

        actual = tokenize("안녕ㅋㅋ")
        expected = listOf(
            KoreanToken("안녕", Noun, 0, 2),
            KoreanToken("ㅋㅋ", KoreanParticle, 2, 2)
        )
        actual shouldContainSame expected

        actual = tokenize("라고만")
        expected = listOf(KoreanToken("라고만", Eomi, 0, 3))
        actual shouldContainSame expected

        actual = tokenize("\"라면서 외쳤다")
        expected = listOf(
            KoreanToken("\"", Punctuation, 0, 1),
            KoreanToken("라면서", Eomi, 1, 3),
            KoreanToken(" ", Space, 4, 1),
            KoreanToken("외쳤다", Verb, 5, 3, stem = "외치다")
        )
        actual shouldContainSame expected

        actual = tokenize("사랑해")
        expected = listOf(
            KoreanToken("사랑", Noun, 0, 2),
            KoreanToken("해", Verb, 2, 1, stem = "하다")
        )
        actual shouldContainSame expected
    }

    @Test
    fun `should handle unknown nouns`() {
        var actual = tokenize("개컁컁아")
        var expected = listOf(
            KoreanToken("개컁컁", Noun, 0, 3, unknown = true),
            KoreanToken("아", Josa, 3, 1)
        )
        actual shouldContainSame expected

        actual = tokenize("안녕하세요쿛툐캬님")
        expected = listOf(
            KoreanToken("안녕하세요", Adjective, 0, 5, stem = "안녕하다"),
            KoreanToken("쿛툐캬", Noun, 5, 3, unknown = true),
            KoreanToken("님", Suffix, 8, 1)
        )
        actual shouldContainSame expected
    }

    @Test
    fun `should handle edge cases`() {
        var actual = tokenize("이승기가")
        var expected = listOf(
            KoreanToken("이승기", Noun, 0, 3),
            KoreanToken("가", Josa, 3, 1)
        )
        actual shouldContainSame expected

        actual = tokenize("야이건뭐")
        expected = listOf(
            KoreanToken("야", Exclamation, 0, 1),
            KoreanToken("이건", Noun, 1, 2),
            KoreanToken("뭐", Noun, 3, 1)
        )
        actual shouldContainSame expected

        actual = tokenize("아이럴수가")
        expected = listOf(
            KoreanToken("아", Exclamation, 0, 1),
            KoreanToken("이럴수가", Adjective, 1, 4, stem = "이렇다")
        )
        actual shouldContainSame expected

        actual = tokenize("보다가")
        expected = listOf(KoreanToken("보다가", Verb, 0, 3, stem = "보다"))
        actual shouldContainSame expected

        // BUG : "하" 를 PreEomi 로 판단하는 경우가 가끔 있다
        //    actual = tokenize("하...")
        //    expected = listOf(KoreanToken("하", PreEomi, 0, 1),
        //                          KoreanToken("...", Punctuation, 1, 3))
        //    assertThat(actual).isEqualTo(expected)
        //
        //    actual = tokenize("하 ...")
        //    expected = listOf(KoreanToken("하", Exclamation, 0, 1),
        //                          KoreanToken(" ", Space, 1, 1),
        //                          KoreanToken("...", Punctuation, 2, 3))
        //    assertThat(actual).isEqualTo(expected)

        actual = tokenize("시전하는")
        expected = listOf(
            KoreanToken("시전", Noun, 0, 2),
            KoreanToken("하는", Verb, 2, 2, stem = "하다")
        )
        actual shouldContainSame expected
    }

    @Test
    fun `should be able to tokenize long non-space-correctable ones`() {
        val actual = tokenize("훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌쩍훌")
        val expected =
            (0 until 24).map { KoreanToken("훌쩍", Noun, it * 2, 2) } + KoreanToken("훌", Noun, 48, 1, unknown = true)
        actual shouldContainSame expected
    }

    @Test
    fun `should tokenize edge cases`() {
        val actual = tokenize("해쵸쵸쵸쵸쵸쵸쵸쵸춏")
        val expected = listOf(
            KoreanToken("해", Noun, 0, 1),
            KoreanToken("쵸쵸쵸쵸쵸쵸쵸쵸", Noun, 1, 8, unknown = true),
            KoreanToken("춏", Noun, 9, 1, unknown = true)
        )
        actual shouldContainSame expected
    }

    @Test
    fun `should add user-added nouns to dictionary`() {
        KoreanDictionaryProvider.koreanDictionary[Noun]!!.contains("뇬뇨").shouldBeFalse()
        KoreanDictionaryProvider.koreanDictionary[Noun]!!.contains("츄쵸").shouldBeFalse()

        var actual = tokenize("뇬뇨뇬뇨뇬뇨뇬뇨츄쵸")
        var expected = listOf(
            KoreanToken("뇬뇨뇬뇨뇬뇨뇬뇨", Noun, 0, 8, unknown = true),
            KoreanToken("츄쵸", Noun, 8, 2, unknown = true)
        )
        actual shouldContainSame expected

        KoreanDictionaryProvider.addWordsToDictionary(Noun, listOf("뇬뇨", "츄쵸"))

        KoreanDictionaryProvider.koreanDictionary[Noun]!!.contains("뇬뇨").shouldBeTrue()
        KoreanDictionaryProvider.koreanDictionary[Noun]!!.contains("츄쵸").shouldBeTrue()

        actual = tokenize("뇬뇨뇬뇨뇬뇨뇬뇨츄쵸")
        expected = listOf(
            KoreanToken("뇬뇨", Noun, 0, 2),
            KoreanToken("뇬뇨", Noun, 2, 2),
            KoreanToken("뇬뇨", Noun, 4, 2),
            KoreanToken("뇬뇨", Noun, 6, 2),
            KoreanToken("츄쵸", Noun, 8, 2)
        )
        actual shouldContainSame expected
    }

    @Test
    fun `noun-josa unmatched`() {
        var actual = tokenize("울다")
        var expected = listOf(KoreanToken("울다", Verb, 0, 2, stem = "울다"))
        actual shouldContainSame expected

        actual = tokenize("울이다")
        expected = listOf(
            KoreanToken("울", Noun, 0, 1),
            KoreanToken("이다", Josa, 1, 2)
        )
        actual shouldContainSame expected

        actual = tokenize("사랑으로")
        expected = listOf(
            KoreanToken("사랑", Noun, 0, 2),
            KoreanToken("으로", Josa, 2, 2)
        )
        actual shouldContainSame expected

        actual = tokenize("사랑로")
        expected = listOf(
            KoreanToken("사랑", Noun, 0, 2),
            KoreanToken("로", Noun, 2, 1)
        )
        actual shouldContainSame expected

        actual = tokenize("고화질로")
        expected = listOf(
            KoreanToken("고화질", Noun, 0, 3),
            KoreanToken("로", Josa, 3, 1)
        )
        actual shouldContainSame expected
    }
}
