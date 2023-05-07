package io.bluetape4k.spring.retrofit2.services.httpbin

import io.bluetape4k.spring.retrofit2.Retrofit2Client
import retrofit2.http.GET
import java.io.Serializable

data class IpAddress(val origin: String): Serializable

@Retrofit2Client(name = "httpbin", baseUrl = "\${bluetape4k.retrofit2.services.httpbin}")
interface HttpbinApi {
    @GET("/ip")
    suspend fun getLocalIpAddress(): IpAddress
}
