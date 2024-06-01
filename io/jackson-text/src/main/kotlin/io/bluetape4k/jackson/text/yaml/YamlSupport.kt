package io.bluetape4k.jackson.text.yaml

fun String.trimYamlDocMarker(): String {
    var doc = this.trim()
    if (startsWith("---")) {
        doc = doc.substring(3)
    }
    return doc.trim()
}
