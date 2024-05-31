package io.wrtn.kommons.captcha

import java.io.Serializable

interface Captcha<T>: Serializable {
    val code: String
    val content: T
}
