# Module bluetape4k-hyperscan

[hyperscan-java](https://github.com/gliwka/hyperscan-java) 를 Kotlin으로 포팅했습니다.

---

[hyperscan](https://github.com/intel/hyperscan) 는 고성능의 복수의 정규식 매칭용 라이브러리입니다.
이 라이브러리는 대량의 정규식을 동시에 매칭하거나, 데이터 스트림을 통해 정규식을 매칭할 수 있습니다.

[hyperscan](https://github.com/intel/hyperscan) 프로젝트를 Kotlin 에서 사용하기 위해 wrapping 했습니다.

## 사용법

복수의 정규식을 동시에 매칭하는지 검사하는 방법은 다음과 같습니다.

```kotlin
val patterns = listOf(
    Pattern.compile("이 숫자는 ([0-9]+) 입니다", Pattern.CASE_INSENSITIVE),
    Pattern.compile("이 색상은 (파랑|빨강|오렌지)색입니다")
)

withPatternFilter(patterns) {
    val matchers = filter("이 숫자는 1234 입니다")
    assertHasPattern(patterns[0], matchers)
}

withPatternFilter(patterns) {
    val matchers = filter("이 색상은 빨강색입니다")
    assertHasPattern(patterns[1], matchers)
}
```

### 금칙어 필터링

금칙어를 적발하는 복수의 정규식들을 이용하여, 문자열 중에 금칙어가 있는지 검사하는 `BlockwordFilter` 를 제공합니다.

```kotlin
@ParameterizedTest
@CsvSource(
    value = [
        "너는 바보인가? ㅅㅂ 웃기네:ㅅㅂ",
        "너는 바보인가? ㅅㅂㅅㅋ:ㅅㅂㅅㅋ",
        "너는 퐁퐁넘이야:퐁퐁넘",

    ], delimiter = ':'
)
fun `금칙어가 들어간 문장에서 금칙어 추출하기`(text: String, expected: String) {
    log.debug { "text=$text, expected=$expected" }

    val blockwords = blockwordFilter.filter(text)
    log.debug { "block words=${blockwords.joinToString()}" }
    blockwords shouldContain expected
} 
```

이렇게 금칙어를 적발하기 위한 정규식은 `src/main/resources/block/block_patterns.txt` 에 정의되어 있습니다.

```txt
(ㅅㅂ|ㅆㅂ|씨발|시발)[0-9\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]*
(개|ㅅㅂ|ㅆㅂ|씨발|시발)[0-9\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]*(새끼|시키|스키|ㅅㅋ|ㅅㄲ|년|련|ㄴㄴ)
(병신|빙신|ㅂㅅ)[0-9\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]*(새끼|시키|스키|ㅅㄲ|ㅅㅋ|년|련|ㄴㄴ)
(후다)[0-9\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]*(새끼|시키|스키|ㅅㄲ|ㅅㅋ|년|련|ㄴㄴ)
대마[0-9\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]*(초|초잎|잎|��)
룸[0-9\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]*(싸|사|쌀|살)롱
짱[0-9\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]*(개|깨|게|께)
느[0-9\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]*금[0-9\{\}\[\]\/?.,;:|\)*~`!^\-_+<>@\#$%&\\\=\(\'\"]*마
퐁퐁(남|넘|열|단|시티)
여성 *혐오자
한남 *충
```

### 유해사이트 적발

문장중에 유해사이트가 있는지 검사하기 위해 `HarmfulSiteFilter` 를 제공합니다.
유해사이트는 `src/main/resources/block/harmful_domains.txt` 에 정의되어 있습니다.

```kotlin
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
```

## 참고

유해 사이트, 금칙어 적발을 위해서는

1. 정규식으로 검출
2. 형태소분석기를 통한 검출

을 수행할 수 있습니다. 정규식은 빠르지만 모든 단어에 대한 정규식이 필요하고, 형태소 분석기는 검출 확률은 낮지만 사전이 풍부해서 서로 보완적이라 볼 수 있습니다.

## TODO

실제 금칙어 처리를 위해서는 형태소분석기에서는 사전 관리, 정규식 패턴 검사는 정규식을 관리하는 방안이 필요하고, 이를 동적으로 적용할 수 있는 기능이 필요합니다.
이를 위해 형태소 분석기에서는 Dictionary 에 동적으로 단어를 추가할 수 있는 기능이 필요하고, 정규식에서는 정규식을 동적으로 추가할 수 있는 기능이 필요합니다.
