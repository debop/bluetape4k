package io.bluetape4k.workshop.graphql.dgs.datafetchers

import com.netflix.graphql.dgs.DgsComponent
import com.netflix.graphql.dgs.DgsMutation
import com.netflix.graphql.dgs.InputArgument
import io.bluetape4k.codec.encodeBase62
import io.bluetape4k.logging.KLogging
import io.bluetape4k.support.uninitialized
import io.bluetape4k.workshop.graphql.dgs.generated.types.Image
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

/**
 * GraphQL Mutation 으로 File Upload 를 처라하기 위한 Data Fetcher
 */
@DgsComponent
class ArtworkUploadDataFetcher {

    companion object: KLogging() {
        private const val UPLOAD_PATH = "uploaded-images"
        private val syncObj = Any()
    }

    @Autowired
    private val webProperties: WebProperties = uninitialized()

    private val uploadPath: Path by lazy {
        Paths.get(webProperties.resources.staticLocations.firstOrNull() ?: UPLOAD_PATH)
    }

    /**
     * [MultipartFile] Upload 를 처리합니다.
     *
     * @param showId Show ID
     * @param upload Upload 된 파일
     * @return [showId]와 관련된 Upload 된 모든 파일 목록
     */
    @DgsMutation
    fun uploadArtwork(@InputArgument showId: Int, @InputArgument upload: MultipartFile): List<Image> {
        synchronized(syncObj) {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath)
            }
        }

        // Upload 된 파일을 서버에 저장한다
        val filePrefix = "show-$showId-${UUID.randomUUID().encodeBase62()}"
        val fileExtension = upload.originalFilename?.substringAfterLast(".") ?: "jpg"
        val localFilePath = uploadPath.resolve("$filePrefix.$fileExtension")

        Files.newOutputStream(localFilePath).use {
            it.write(upload.bytes)
        }

        // showId 와 관련된 모든 Image 파일 정보를 반환한다
        return Files.list(uploadPath)
            .filter { it.fileName.startsWith("show-$showId-") }
            .map { Image(it.fileName.toString()) }
            .toList()
    }
}
