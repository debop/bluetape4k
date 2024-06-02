package io.bluetape4k.tokenizer.japanese.tokenizer

import com.atilika.kuromoji.TokenBase

fun TokenBase.isNoun(): Boolean {
    return this.allFeatures.contains("名詞")
}

fun TokenBase.isVerb(): Boolean {
    return this.allFeatures.contains("動詞")
}

fun TokenBase.isNounOrVerb(): Boolean {
    return this.isNoun() || this.isVerb()
}

fun TokenBase.isAdjective(): Boolean {
    return this.allFeatures.contains("形容詞")
}

fun TokenBase.isJosa(): Boolean {
    return this.allFeatures.contains("助詞")
}

fun TokenBase.isConjugate(): Boolean {
    return this.allFeatures.contains("助動詞")
}

fun TokenBase.isPunctuation(): Boolean {
    return this.allFeatures.contains("記号")
}
