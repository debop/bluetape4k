package io.bluetape4k.lang

import java.net.URL

fun getSystemURLs(): Array<URL> =
    (ClassLoader.getSystemClassLoader() as? java.net.URLClassLoader)?.urLs ?: emptyArray()

fun getThreadURLs(): Array<URL> =
    (Thread.currentThread().contextClassLoader as? java.net.URLClassLoader)?.urLs ?: emptyArray()
