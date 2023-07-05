package io.bluetape4k.openai.clients.retrofit2.sync

import io.bluetape4k.openai.api.models.ListResult
import io.bluetape4k.openai.api.models.file.File
import io.bluetape4k.openai.api.models.file.FileId
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface FileSyncApi {

    companion object {
        private const val FILES_PATH = "/v1/files"
    }

    @GET(FILES_PATH)
    fun getFiles(): ListResult<File>

    @Multipart
    @POST(FILES_PATH)
    fun uploadFile(@Part("purpose") purpose: RequestBody, @Part file: MultipartBody.Part): File

    @DELETE("$FILES_PATH/{fileId}")
    fun deleteFile(@Path("fileId") fileId: FileId): File

    @GET("$FILES_PATH/{fileId}")
    fun getFile(@Path("fileId") fileId: FileId): File
}
