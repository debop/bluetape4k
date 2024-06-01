package io.bluetape4k.http.hc5.fluent

import org.apache.hc.client5.http.fluent.Request
import org.apache.hc.core5.http.Method
import java.net.URI

fun requestOf(method: Method, uri: URI): Request = Request.create(method, uri)
fun requestOf(methodName: String, uri: URI): Request = Request.create(methodName, uri)
fun requestOf(methodName: String, uri: String): Request = Request.create(methodName, uri)

fun requestGet(uri: URI): Request = Request.get(uri)
fun requestGet(uri: String): Request = Request.get(uri)

fun requestHead(uri: URI): Request = Request.head(uri)
fun requestHead(uri: String): Request = Request.head(uri)

fun requestPost(uri: URI): Request = Request.post(uri)
fun requestPost(uri: String): Request = Request.post(uri)

fun requestPatch(uri: URI): Request = Request.patch(uri)
fun requestPatch(uri: String): Request = Request.patch(uri)

fun requestPut(uri: URI): Request = Request.put(uri)
fun requestPut(uri: String): Request = Request.put(uri)

fun requestTrace(uri: URI): Request = Request.trace(uri)
fun requestTrace(uri: String): Request = Request.trace(uri)

fun requestDelete(uri: URI): Request = Request.delete(uri)
fun requestDelete(uri: String): Request = Request.delete(uri)

fun requestOptions(uri: URI): Request = Request.options(uri)
fun requestOptions(uri: String): Request = Request.options(uri)
