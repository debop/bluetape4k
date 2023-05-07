package io.bluetape4k.infra.micrometer.instrument.retrofit2

import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory
import io.bluetape4k.infra.micrometer.AbstractMicrometerTest
import io.bluetape4k.io.retrofit2.clients.vertx.vertxCallFactoryOf
import io.bluetape4k.io.retrofit2.defaultJsonConverterFactory
import io.bluetape4k.io.retrofit2.executeAsync
import io.bluetape4k.io.retrofit2.retrofit
import io.bluetape4k.io.retrofit2.service
import io.bluetape4k.junit5.coroutines.runSuspendWithIO
import io.bluetape4k.logging.KLogging
import io.bluetape4k.logging.debug
import io.bluetape4k.support.classIsPresent
import io.micrometer.core.instrument.simple.SimpleMeterRegistry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.http.GET
import java.io.Serializable
import java.util.*

class Retrofit2MetricsTest: AbstractMicrometerTest() {

    companion object: KLogging() {
        private const val REPEAT_SIZE = 5
    }

    object TestService {

        const val TEST_COUNT = 30
        const val BintrayApiBaseUrl = "https://httpbin.org"

        data class IpAddress(val origin: String): Serializable

        interface HttpbinApi {
            @GET("/ip")
            fun getLocalIpAddress(): Call<IpAddress>
        }

        interface CoroutineHttpbinApi {
            @GET("/ip")
            suspend fun getLocalIpAddress(): IpAddress
        }

        interface ReactorHttpbinApi {
            @GET("/ip")
            fun getLocalIpAddress(): Mono<IpAddress>
        }
    }

    private fun isPresentRetrofitAdapterRxJava2(): Boolean =
        classIsPresent("retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory")

    private fun createRetrofit(factory: CallAdapter.Factory): Retrofit {
        return retrofit(TestService.BintrayApiBaseUrl) {
            callFactory(vertxCallFactoryOf())
            addConverterFactory(defaultJsonConverterFactory)
            addCallAdapterFactory(factory)

            if (isPresentRetrofitAdapterRxJava2()) {
                addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())
            }
            if (classIsPresent("com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory")) {
                addCallAdapterFactory(ReactorCallAdapterFactory.createAsync())
            }
        }
    }

    @Test
    fun `measure metrics for sync method`() {
        val registry = SimpleMeterRegistry()
        val factory = MicrometerRetrofitMetricsFactory(registry)
        val httpbinApi = createRetrofit(factory).service<TestService.HttpbinApi>()

        val call = httpbinApi.getLocalIpAddress()
        call.shouldNotBeNull()
        val ipAddress = call.execute().body()
        ipAddress.shouldNotBeNull()
        log.debug { "ip address=$ipAddress" }

        repeat(REPEAT_SIZE) {
            httpbinApi.getLocalIpAddress().execute()
        }

        registry.meters.forEach { meter ->
            log.debug { "id=${meter.id}, tags=${meter.measure().joinToString()}" }
        }
        registry[MicrometerRetrofitMetricsRecorder.METRICS_KEY].timer().shouldNotBeNull()
    }

    @Test
    fun `measure metrics for async method`() = runSuspendWithIO {
        val registry = SimpleMeterRegistry()
        val factory = MicrometerRetrofitMetricsFactory(registry)
        val httpbinApi = createRetrofit(factory).service<TestService.HttpbinApi>()

        val call = httpbinApi.getLocalIpAddress()
        call.shouldNotBeNull()
        val ipAddress = call.executeAsync().await().body()
        ipAddress.shouldNotBeNull()
        log.debug { "ip address=$ipAddress" }

        List(REPEAT_SIZE) {
            httpbinApi.getLocalIpAddress().executeAsync()
        }.map { it.await() }

        registry.meters.forEach { meter ->
            log.debug { "id=${meter.id}, tags=${meter.measure().joinToString()}" }
        }
        registry[MicrometerRetrofitMetricsRecorder.METRICS_KEY].timer().shouldNotBeNull()
    }

    @Test
    fun `measure metrics for coroutine method`() = runSuspendWithIO {
        val registry = SimpleMeterRegistry()
        val factory = MicrometerRetrofitMetricsFactory(registry)
        val httpbinApi = createRetrofit(factory).service<TestService.CoroutineHttpbinApi>()

        val ipAddress = httpbinApi.getLocalIpAddress()
        log.debug { "ip addresss=$ipAddress" }

        List(REPEAT_SIZE) {
            launch(Dispatchers.IO) {
                httpbinApi.getLocalIpAddress()
            }
        }.joinAll()

        registry.meters.forEach { meter ->
            log.debug { "id=${meter.id}, tags=${meter.measure().joinToString()}" }
        }
        registry[MicrometerRetrofitMetricsRecorder.METRICS_KEY].timer().shouldNotBeNull()
    }

    @Disabled("Reactive CallAdapter 와 같이 사용할 수 없습니다")
    @Test
    fun `measure metrics for reactive method`() = runBlocking<Unit> {
        val registry = SimpleMeterRegistry()
        val factory = MicrometerRetrofitMetricsFactory(registry)
        val httpbinApi = createRetrofit(factory).service<TestService.ReactorHttpbinApi>()

        val ipAddress = httpbinApi.getLocalIpAddress().awaitSingle()
        log.debug { "ip address=$ipAddress" }

        registry.meters.forEach { meter ->
            log.debug { "id=${meter.id}, tags=${meter.measure().joinToString()}" }
        }
        registry[MicrometerRetrofitMetricsRecorder.METRICS_KEY].timer().shouldNotBeNull()
    }

}
