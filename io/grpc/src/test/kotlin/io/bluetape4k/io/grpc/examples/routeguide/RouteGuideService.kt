package io.bluetape4k.io.grpc.examples.routeguide

import com.google.common.base.Stopwatch
import com.google.common.base.Ticker
import com.google.protobuf.util.Durations
import io.bluetape4k.logging.KLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import org.eclipse.collections.impl.factory.Multimaps
import java.util.concurrent.TimeUnit

class RouteGuideService @JvmOverloads constructor(
    val features: Collection<Feature>,
    val ticker: Ticker = Ticker.systemTicker(),
): RouteGuideGrpcKt.RouteGuideCoroutineImplBase() {

    companion object: KLogging()

    private val routeNotes = Multimaps.mutable.list.of<Point, RouteNote>()

    override suspend fun getFeature(request: Point): Feature {
        return features.find { it.location == request }
            ?: Feature.newBuilder().setLocation(request).build()
    }

    override fun listFeatures(request: Rectangle): Flow<Feature> {
        return features.asFlow().filter { it.exists() && it.location in request }
    }

    override suspend fun recordRoute(requests: Flow<Point>): RouteSummary {
        var pointCount = 0
        var featureCount = 0
        var distance = 0
        var previous: Point? = null
        val stopwatch = Stopwatch.createStarted(ticker)

        requests.collect { request ->
            pointCount++
            if (getFeature(request).exists()) {
                featureCount++
            }
            val prev = previous
            if (prev != null) {
                distance += prev distanceTo request
            }
            previous = request
        }

        return RouteSummary.newBuilder()
            .apply {
                this.pointCount = pointCount
                this.featureCount = featureCount
                this.distance = distance
                this.elapsedTime = Durations.fromMicros(stopwatch.elapsed(TimeUnit.MICROSECONDS))
            }
            .build()
    }

    override fun routeChat(requests: Flow<RouteNote>): Flow<RouteNote> {
        return flow {
            requests.collect { note ->
                val notes = routeNotes.getIfAbsentPutAll(note.location, mutableListOf<RouteNote>())

                // thread-safe snapshot
                notes.forEach { prevNote ->
                    emit(prevNote)
                }

                routeNotes.put(note.location, note)
            }
        }
    }
}
