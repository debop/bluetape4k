package io.bluetape4k.aws.s3

import io.bluetape4k.aws.auth.LocalAwsCredentialsProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.services.s3.S3AsyncClientBuilder
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3ClientBuilder
import software.amazon.awssdk.transfer.s3.S3TransferManager
import java.net.URI
import java.util.concurrent.Executor

/**
 * [S3Client], [S3AsyncClient], [S3TransferManager] 인스턴스를 생성하는 함수를 제공합니다.
 */
object S3Factory {

    /**
     * S3를 동기방식으로 사용하는 [S3Client] 를 생성하는 메소드를 제공합니다.
     */
    object Sync {

        /**
         * [S3Client] 를 생성합니다.
         *
         * @param initializer [S3ClientBuilder]를 이용하여 [S3Client]를 설정합니다.
         * @return [S3Client] 인스턴스
         */
        inline fun create(initializer: S3ClientBuilder.() -> Unit): S3Client {
            return S3Client.builder().apply(initializer).build()
        }

        /**
         * [S3Client] 를 생성합니다.
         *
         * @param endpointOverride      S3 endpoint
         * @param region                S3 region
         * @param credentialsProvider   AWS [AwsCredentialsProvider] 인스턴스
         * @param initializer           [S3ClientBuilder]를 이용하여 [S3Client]를 설정합니다.
         * @return [S3Client] 인스턴스
         */
        inline fun create(
            endpointOverride: URI,
            region: Region = Region.AP_NORTHEAST_2,
            credentialsProvider: AwsCredentialsProvider = LocalAwsCredentialsProvider,
            initializer: S3ClientBuilder.() -> Unit = {},
        ): S3Client {
            return create {
                endpointOverride(endpointOverride)
                region(region)
                credentialsProvider(credentialsProvider)
                initializer()
            }
        }
    }

    /**
     * S3를 비동기방식으로 사용하는 [S3AsyncClient] 를 생성하는 메소드를 제공합니다.
     */
    object Async {

        /**
         * [S3AsyncClient] 를 생성합니다.
         *
         * @param initializer [S3AsyncClientBuilder]를 이용하여 [S3AsyncClient]를 설정합니다.
         * @return [S3AsyncClient] 인스턴스
         */
        inline fun create(initializer: S3AsyncClientBuilder.() -> Unit): S3AsyncClient {
            return S3AsyncClient.builder()
                .accelerate(true)
                .apply(initializer)
                .build()
        }

        /**
         * [S3AsyncClient] 를 생성합니다.
         *
         * @param endpointOverride      S3 endpoint
         * @param region                S3 region
         * @param credentialsProvider   AWS [AwsCredentialsProvider] 인스턴스
         * @param initializer           [S3AsyncClientBuilder]를 이용하여 [S3Client]를 설정합니다.
         * @return [S3AsyncClient] 인스턴스
         */
        inline fun create(
            endpointOverride: URI,
            region: Region = Region.AP_NORTHEAST_2,
            credentialsProvider: AwsCredentialsProvider = LocalAwsCredentialsProvider,
            initializer: S3AsyncClientBuilder.() -> Unit = {},
        ): S3AsyncClient {
            return create {
                endpointOverride(endpointOverride)
                region(region)
                credentialsProvider(credentialsProvider)
                initializer()
            }
        }
    }

    /**
     * [S3TransferManager]를 생성하는 Factory
     */
    object TransferManager {

        /**
         * [S3TransferManager]를 생성합니다.
         *
         * @param  initializer [S3TransferManager] Builder를 이용하여 설정하는 코드 블럭
         * @return [S3TransferManager] 인스턴스
         */
        inline fun create(initializer: S3TransferManager.Builder.() -> Unit): S3TransferManager {
            return S3TransferManager.builder().apply(initializer).build()
        }

        /**
         * [S3TransferManager]를 생성합니다.
         *
         * @param endpointOverride      S3 endpoint
         * @param region                S3 region
         * @param  initializer          [S3TransferManager] Builder를 이용하여 설정하는 코드 블럭
         * @return [S3TransferManager] 인스턴스
         */
        inline fun create(
            endpointOverride: URI,
            region: Region = Region.AP_NORTHEAST_2,
            credentialsProvider: AwsCredentialsProvider = LocalAwsCredentialsProvider,
            executor: Executor = Dispatchers.IO.asExecutor(),
            initializer: S3TransferManager.Builder.() -> Unit = {},
        ): S3TransferManager {
            return create {
                val asyncClient = Async.create {
                    endpointOverride(endpointOverride)
                    region(region)
                    credentialsProvider(credentialsProvider)
                }
                this.s3Client(asyncClient)
                this.executor(executor)
                initializer()
            }
        }
    }
}
