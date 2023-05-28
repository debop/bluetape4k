package io.bluetape4k.tokenizer.korean.tokenizer

import io.bluetape4k.logging.debug
import io.bluetape4k.tokenizer.korean.TestBase
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanChunker.ChunkMatch
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanChunker.POS_PATTERNS
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanChunker.findAllPatterns
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanChunker.getChunks
import io.bluetape4k.tokenizer.korean.tokenizer.KoreanChunker.getChunksByPos
import io.bluetape4k.tokenizer.korean.utils.KoreanPos
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Alpha
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.CashTag
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Email
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Hashtag
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Korean
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.KoreanParticle
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Number
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Punctuation
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.ScreenName
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.Space
import io.bluetape4k.tokenizer.korean.utils.KoreanPos.URL
import kotlinx.coroutines.test.runTest
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldContainSame
import org.junit.jupiter.api.Test
import java.util.regex.Matcher

class KoreanChunkerTest: TestBase() {

    private fun getPatternMatcher(pos: KoreanPos, text: String): Matcher {
        return POS_PATTERNS[pos]!!.matcher(text)
    }

    @Test
    fun `findAllPatterns should find all patterns`() {
        val matcher = getPatternMatcher(URL, "스팀(http://store.steampowered.com)에서 드디어 여름세일을 시작합니다.")
        var actual = findAllPatterns(matcher, URL)
        var expected = listOf(ChunkMatch(2, 32, "(http://store.steampowered.com", URL))
        actual shouldContainSame expected

        actual = findAllPatterns(
            getPatternMatcher(Email, "만약 메일 주소가 하나 있고(예: hong@mail.com) 동시에 수백만명이 메일을 보낸다면 어떻게 될까?"),
            Email
        )
        expected = listOf(ChunkMatch(19, 32, "hong@mail.com", Email))
        actual shouldContainSame expected

        actual = findAllPatterns(
            getPatternMatcher(ScreenName, "트위터 아이디는 언제든지 변경이 가능합니다. @ironman을 @drstrange로 바꿀 수 있습니다."),
            ScreenName
        )
        expected = listOf(
            ChunkMatch(start = 34, end = 45, text = " @drstrange", pos = ScreenName),
            ChunkMatch(start = 24, end = 33, text = " @ironman", pos = ScreenName)
        )
        actual shouldContainSame expected

        actual = findAllPatterns(getPatternMatcher(Hashtag, "구글에는 정말로 이쁜 자전거가 있다. #Google #이쁜자전거 #갖고싶다"), Hashtag)
        expected = listOf(
            ChunkMatch(start = 35, end = 41, text = " #갖고싶다", pos = Hashtag),
            ChunkMatch(start = 28, end = 35, text = " #이쁜자전거", pos = Hashtag),
            ChunkMatch(start = 20, end = 28, text = " #Google", pos = Hashtag)
        )
        actual shouldContainSame expected

        actual = findAllPatterns(
            getPatternMatcher(CashTag, "주식정보 트윗 안내 : Twitter의 주식은 \$twtr, Apple의 주식은 \$appl 입니다."),
            CashTag
        )
        expected = listOf(
            ChunkMatch(start = 43, end = 49, text = " \$appl", pos = CashTag),
            ChunkMatch(start = 25, end = 31, text = " \$twtr", pos = CashTag)
        )

        actual shouldContainSame expected


        actual = findAllPatterns(getPatternMatcher(Korean, "Hey! Can you speak Korean? 한국말! 오케이?"), Korean)
        expected = listOf(
            ChunkMatch(start = 32, end = 35, text = "오케이", pos = Korean),
            ChunkMatch(start = 27, end = 30, text = "한국말", pos = Korean)
        )

        actual shouldContainSame expected

        actual = findAllPatterns(getPatternMatcher(KoreanParticle, "ㅋㅋ보다는 ㅎㅎ를 쓰라는데 무슨 차이인가요?"), KoreanParticle)
        expected = listOf(
            ChunkMatch(start = 6, end = 8, text = "ㅎㅎ", pos = KoreanParticle),
            ChunkMatch(start = 0, end = 2, text = "ㅋㅋ", pos = KoreanParticle)
        )
        actual shouldContainSame expected

        actual = findAllPatterns(
            getPatternMatcher(Number, "6월 21일 개봉한 트랜스포머5:최후의 기사가 혹평 속에서도 박스오피스 1위를 달리고 있다."),
            Number
        )
        expected = listOf(
            ChunkMatch(40, 41, "1", Number),
            ChunkMatch(16, 17, "5", Number),
            ChunkMatch(3, 6, "21일", Number),
            ChunkMatch(0, 2, "6월", Number)
        )
        actual shouldContainSame expected

        actual = findAllPatterns(
            getPatternMatcher(Alpha, "육회가 'six times', 곰탕이 'bear thang' 이라고? 아오! 정말 부끄러운 줄 알아랏!!"),
            Alpha
        )
        expected = listOf(
            ChunkMatch(27, 32, "thang", Alpha),
            ChunkMatch(22, 26, "bear", Alpha),
            ChunkMatch(9, 14, "times", Alpha),
            ChunkMatch(5, 8, "six", Alpha)
        )
        actual shouldContainSame expected

        actual = findAllPatterns(
            getPatternMatcher(Punctuation, "비가 내리고... 음악이 흐르면... 난 당신을 생각해요~~"),
            Punctuation
        )
        expected = listOf(
            ChunkMatch(31, 33, "~~", Punctuation),
            ChunkMatch(17, 20, "...", Punctuation),
            ChunkMatch(6, 9, "...", Punctuation)
        )
        actual shouldContainSame expected
    }

    @Test
    fun `getChunks should split a string into Korean-sensitive chunks`() = runTest {
        var actual = getChunks("안녕? iphone6안녕? 세상아?")
        var expected = listOf("안녕", "?", " ", "iphone", "6", "안녕", "?", " ", "세상아", "?")

        actual shouldContainSame expected

        actual = getChunks("This is an 한국어가 섞인 English tweet.")
        expected = listOf("This", " ", "is", " ", "an", " ", "한국어가", " ", "섞인", " ", "English", " ", "tweet", ".")

        actual shouldContainSame expected

        actual = getChunks("이 日本것은 日本語Eng")
        expected = listOf("이", " ", "日本", "것은", " ", "日本語", "Eng")

        actual shouldContainSame expected

        actual = getChunks("무효이며")
        expected = listOf("무효이며")

        actual shouldContainSame expected

        actual = getChunks("#해쉬태그 이라는 것 #hash @hello 123 이런이런 #여자최애캐_5명으로_취향을_드러내자")
        expected = listOf(
            "#해쉬태그", " ", "이라는", " ", "것", " ", "#hash", " ", "@hello", " ", "123", " ",
            "이런이런", " ", "#여자최애캐_5명으로_취향을_드러내자"
        )

        log.debug { "actual=$actual" }
        actual shouldContainSame expected
    }

    @Test
    fun `getChunksByPos should extract chunks with a POS tag`() = runTest {

        var actual = getChunksByPos("openkoreantext.org에서 API를 테스트 할 수 있습니다.", URL)
        var expected = listOf(KoreanToken(text = "openkoreantext.org", pos = URL, offset = 0, length = 18))
        actual shouldContainSame expected

        actual = getChunksByPos("메일 주소 mechanickim@openkoreantext.org로 문의주시거나", Email)
        expected = listOf(KoreanToken(text = "mechanickim@openkoreantext.org", pos = Email, offset = 6, length = 30))
        actual shouldContainSame expected

        actual = getChunksByPos("해시태그는 이렇게 생겼습니다. #나는_해적왕이_될_사나이다", Hashtag)
        expected = listOf(KoreanToken(text = "#나는_해적왕이_될_사나이다", pos = Hashtag, offset = 17, length = 15))
        actual shouldContainSame expected

        actual = getChunksByPos("캐쉬태그는 주식정보 트윗할 때 사용합니다. \$twtr", CashTag)
        expected = listOf(KoreanToken(text = "\$twtr", pos = CashTag, offset = 24, length = 5))
        actual shouldContainSame expected

        actual = getChunksByPos("Black action solier 출두요~!", Korean)
        expected = listOf(KoreanToken(text = "출두요", pos = Korean, offset = 20, length = 3))
        actual shouldContainSame expected

        actual = getChunksByPos("Black action solier 출두요~! ㅋㅋ", KoreanParticle)
        expected = listOf(KoreanToken(text = "ㅋㅋ", pos = KoreanParticle, offset = 26, length = 2))
        actual shouldContainSame expected

        actual = getChunksByPos("최근 발매된 게임 '13일의 금요일'은 43,000원에 스팀에서 판매중입니다.", Number)
        expected = listOf(
            KoreanToken("13일", Number, 11, 3),
            KoreanToken("43,000원", Number, 22, 7)
        )
        actual shouldContainSame expected

        actual = getChunksByPos("드래곤볼 Z", Alpha)
        expected = listOf(KoreanToken("Z", Alpha, 5, 1))
        actual shouldContainSame expected

        actual = getChunksByPos("나의 일기장 안에 모든 말을 다 꺼내어 줄 순 없지만... 사랑한다는 말 이에요.", Punctuation)
        expected = listOf(
            KoreanToken("...", Punctuation, 29, 3),
            KoreanToken(".", Punctuation, 44, 1)
        )
        actual shouldContainSame expected

    }

    @Test
    fun `getChunks should extract numbers`() = runTest {
        getChunks("300위안짜리 밥") shouldBeEqualTo listOf("300위안", "짜리", " ", "밥")
        getChunks("200달러와 300유로") shouldBeEqualTo listOf("200달러", "와", " ", "300유로")
        getChunks("$200이나 한다") shouldBeEqualTo listOf("$200", "이나", " ", "한다")
        getChunks("300옌이었다.") shouldBeEqualTo listOf("300옌", "이었다", ".")
        getChunks("3,453,123,123원 3억3천만원") shouldBeEqualTo listOf("3,453,123,123원", " ", "3억", "3천만원")

        getChunks("6/4 지방선거") shouldBeEqualTo listOf("6/4", " ", "지방선거")
        getChunks("6.4 지방 선거") shouldBeEqualTo listOf("6.4", " ", "지방", " ", "선거")
        getChunks("6-4 지방 선거") shouldBeEqualTo listOf("6-4", " ", "지방", " ", "선거")
        getChunks("6.25 전쟁 4.19 의거") shouldBeEqualTo listOf("6.25", " ", "전쟁", " ", "4.19", " ", "의거")
        getChunks("1968년 10월 14일") shouldBeEqualTo listOf("1968년", " ", "10월", " ", "14일")
        getChunks("62:45의 결과") shouldBeEqualTo listOf("62:45", "의", " ", "결과")

        val expected =
            listOf(" ", "여러", " ", "칸", "  ", "띄어쓰기", ",", "   ", "하나의", " ", "Space", "묶음으로", " ", "처리됩니다", ".")
        val actual = getChunks(" 여러 칸  띄어쓰기,   하나의 Space묶음으로 처리됩니다.")
        actual shouldContainSame expected
    }

    @Test
    fun `should find chunks with POS tags`() = runTest {
        var actual = """
                 |한국어와 English와 1234와 pic.twitter.com
                 |http://news.kukinews.com/article/view.asp?page=1&gCode=soc&arcid=0008599913&code=41121111
                 |hohyonryu@twitter.com 갤럭시 S5
                 """.trimMargin().replace('\n', ' ')

        var expected = listOf(
            KoreanToken("한국어와", Korean, 0, 4),
            KoreanToken(" ", Space, 4, 1),
            KoreanToken("English", Alpha, 5, 7),
            KoreanToken("와", Korean, 12, 1),
            KoreanToken(" ", Space, 13, 1),
            KoreanToken("1234", Number, 14, 4),
            KoreanToken("와", Korean, 18, 1),
            KoreanToken(" ", Space, 19, 1),
            KoreanToken("pic.twitter.com", URL, 20, 15),
            KoreanToken(" ", Space, 35, 1),
            KoreanToken(
                "http://news.kukinews.com/article/view.asp?page=1&gCode=soc&arcid=0008599913&code=41121111",
                URL,
                36,
                89
            ),
            KoreanToken(" ", Space, 125, 1),
            KoreanToken("hohyonryu@twitter.com", Email, 126, 21),
            KoreanToken(" ", Space, 147, 1),
            KoreanToken("갤럭시", Korean, 148, 3),
            KoreanToken(" ", Space, 151, 1),
            KoreanToken("S", Alpha, 152, 1),
            KoreanToken("5", Number, 153, 1)
        )


        KoreanChunker.chunk(actual) shouldBeEqualTo expected

        actual = "우와!!! 완전ㅋㅋㅋㅋ"
        expected = listOf(
            KoreanToken("우와", Korean, 0, 2),
            KoreanToken("!!!", Punctuation, 2, 3),
            KoreanToken(" ", Space, 5, 1),
            KoreanToken("완전", Korean, 6, 2),
            KoreanToken("ㅋㅋㅋㅋ", KoreanParticle, 8, 4)
        )

        KoreanChunker.chunk(actual) shouldBeEqualTo expected

        actual = "@nlpenguin @edeng #korean_tokenizer_rocks 우하하"
        expected = listOf(
            KoreanToken("@nlpenguin", ScreenName, 0, 10),
            KoreanToken(" ", Space, 10, 1),
            KoreanToken("@edeng", ScreenName, 11, 6),
            KoreanToken(" ", Space, 17, 1),
            KoreanToken("#korean_tokenizer_rocks", Hashtag, 18, 23),
            KoreanToken(" ", Space, 41, 1),
            KoreanToken("우하하", Korean, 42, 3)
        )

        KoreanChunker.chunk(actual) shouldBeEqualTo expected
    }

    @Test
    fun `should detect Korean-specific punctuations`() = runTest {
        val actual = KoreanChunker.chunk("중·고등학교에서…")
        val expected = listOf(
            KoreanToken("중", Korean, 0, 1),
            KoreanToken("·", Punctuation, 1, 1),
            KoreanToken("고등학교에서", Korean, 2, 6),
            KoreanToken("…", Punctuation, 8, 1)
        )

        actual shouldContainSame expected
    }
}
