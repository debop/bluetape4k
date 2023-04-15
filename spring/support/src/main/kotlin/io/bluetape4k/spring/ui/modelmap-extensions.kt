package io.bluetape4k.spring.ui

import org.springframework.ui.ModelMap

fun ModelMap.addAttributes(vararg pairs: Pair<String, Any?>): ModelMap =
    addAllAttributes(pairs.toMap())

fun ModelMap.mergeAttributes(vararg pairs: Pair<String, Any?>): ModelMap =
    mergeAttributes(pairs.toMap())
