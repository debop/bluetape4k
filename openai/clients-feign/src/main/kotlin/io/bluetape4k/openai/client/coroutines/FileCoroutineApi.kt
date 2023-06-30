package io.bluetape4k.openai.client.coroutines

import feign.Headers
import feign.Param
import feign.RequestLine
import io.bluetape4k.openai.api.models.ListResult
import io.bluetape4k.openai.api.models.file.File
import io.bluetape4k.openai.api.models.file.Purpose
import org.apache.hc.client5.http.entity.mime.MultipartPart

interface FileCoroutineApi {

    companion object {
        private const val BASE_PATH = "/v1/files"
    }

    @RequestLine("GET $BASE_PATH")
    suspend fun listFiles(): ListResult<File>

    // https://www.baeldung.com/java-feign-file-upload
    // 
    @RequestLine("POST $BASE_PATH")
    @Headers("Content-Type: multipart/form-data")
    suspend fun uploadFile(@Param("purpose") purpose: Purpose, @Param("file") multipartPart: MultipartPart): File
}
