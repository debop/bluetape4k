package io.bluetape4k.aws.http

object AwsCrtSdkHttpServices {
    init {
        System.setProperty(
            "software.amazon.awssdk.http.async.service.impl",
            "software.amazon.awssdk.http.crt.AwsCrtSdkHttpService"
        )
    }
}
