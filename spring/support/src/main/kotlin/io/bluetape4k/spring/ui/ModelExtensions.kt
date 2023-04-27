package io.bluetape4k.spring.ui

import org.springframework.ui.Model

fun Model.addAttributes(vararg pairs: Pair<String, Any?>): Model =
    addAllAttributes(pairs.toMap())

fun Model.mergeAttributes(vararg pairs: Pair<String, Any?>): Model =
    mergeAttributes(pairs.toMap())
