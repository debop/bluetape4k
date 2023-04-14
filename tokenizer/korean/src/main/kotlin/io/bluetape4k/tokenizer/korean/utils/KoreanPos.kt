package io.bluetape4k.tokenizer.korean.utils

import java.io.Serializable


/**
 * Korean Part-of-Speech
 *
 * N Noun: 명사 (Nouns, Pronouns, Company Names, Proper Noun, Person Names, Numerals, Standalone, Dependent)
 * V Verb: 동사 (하, 먹, 자, 차)
 * J Adjective: 형용사 (예쁘다, 크다, 작다)
 * A Adverb: 부사 (잘, 매우, 빨리, 반드시, 과연)
 * D Determiner: 관형사 (새, 헌, 참, 첫, 이, 그, 저)
 * E Exclamation: 감탄사 (헐, ㅋㅋㅋ, 어머나, 얼씨구)
 *
 * C Conjunction: 접속사
 *
 * j SubstantiveJosa: 조사 (의, 에, 에서)
 * l AdverbialJosa: 부사격 조사 (~인, ~의, ~일)
 * e Eomi: 어말어미 (다, 요, 여, 하댘ㅋㅋ)
 * r PreEomi: 선어말어미 (었)
 *
 * p NounPrefix: 접두사 ('초'대박)
 * v VerbPrefix: 동사 접두어 ('쳐'먹어)
 * s Suffix: 접미사 (~적)
 *
 * f Foreign: 한글이 아닌 문자들
 *
 * 지시사는 Derterminant로 대체하기로 함
 * Derterminant is used for demonstratives.
 *
 * Korean: Korean chunk (candidate for parsing)
 * Foreign: Mixture of non-Korean strings
 * Number: 숫자
 * Emotion: Korean Single Character Emotions (ㅋㅋㅋㅋ, ㅎㅎㅎㅎ, ㅠㅜㅠㅜ)
 * Alpha: Alphabets 알파벳
 * Punctuation: 문장부호
 * Hashtag: Twitter Hashtag 해쉬태그 #Korean
 * ScreenName: Twitter username (@nlpenguin)
 *
 * Unkown: Could not parse the string.
 */
enum class KoreanPos: Serializable {
    // Word leveld Pos
    Noun,
    Verb,
    Adjective,
    Adverb,
    Determiner,
    Exclamation,
    Josa,
    Eomi,
    PreEomi,
    Conjunction,
    Modifier,
    VerbPrefix,
    Suffix,
    Unknown,

    // Chunk level POS
    Korean,
    Foreign,
    Number,
    KoreanParticle,
    Alpha,
    Punctuation,
    Hashtag,
    ScreenName,
    Email,
    URL,
    CashTag,

    // Functional POS
    Space,
    Others,

    ProperNoun
}
