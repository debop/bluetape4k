package io.bluetape4k.io.retrofit2

import com.fasterxml.jackson.databind.json.JsonMapper
import com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory
import io.bluetape4k.io.json.jackson.Jackson
import io.bluetape4k.io.retrofit2.result.ResultCallAdapterFactory
import io.bluetape4k.support.classIsPresent
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import kotlin.reflect.KClass

@JvmField
val defaultConverterFactory: retrofit2.Converter.Factory = jacksonConverterFactoryOf()

fun jacksonConverterFactoryOf(
    mapper: JsonMapper = Jackson.defaultJsonMapper,
): retrofit2.Converter.Factory {
    return JacksonConverterFactory.create(mapper)
}

fun retrofitBuilder(initialize: Retrofit.Builder.() -> Unit): Retrofit.Builder {
    return Retrofit.Builder().apply(initialize)
}

fun retrofitBuilderOf(
    baseUrl: String = "",
    converterFactory: retrofit2.Converter.Factory = defaultConverterFactory,
): Retrofit.Builder {
    return retrofitBuilder {
        if (baseUrl.isNotBlank())
            baseUrl(baseUrl)
        addConverterFactory(converterFactory)
    }
}

fun retrofit(baseUrl: String = "", initialize: Retrofit.Builder.() -> Unit): Retrofit {
    return retrofitBuilderOf(baseUrl).apply(initialize).build()
}

fun retrofitOf(
    baseUrl: String = "",
    callFactory: okhttp3.Call.Factory = okhttp3.OkHttpClient(),
    converterFactory: retrofit2.Converter.Factory = defaultConverterFactory,
    vararg callAdapterFactories: retrofit2.CallAdapter.Factory = arrayOf(ResultCallAdapterFactory()),
): Retrofit {
    return retrofit(baseUrl) {
        callFactory(callFactory)
        addConverterFactory(converterFactory)

        callAdapterFactories.forEach { addCallAdapterFactory(it) }

        if (isPresentRetrofitAdapterRxJava2()) {
            addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        }
        if (isPresentRetrofitAdapterRxJava3()) {
            addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        }
        if (isPresentRetrofitAdapterReactor()) {
            addCallAdapterFactory(ReactorCallAdapterFactory.create())
        }
    }
}

internal fun isPresentRetrofitAdapterRxJava2(): Boolean =
    classIsPresent("retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory")

internal fun isPresentRetrofitAdapterRxJava3(): Boolean =
    classIsPresent("retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory")

internal fun isPresentRetrofitAdapterReactor(): Boolean =
    classIsPresent("com.jakewharton.retrofit2.adapter.reactor.ReactorCallAdapterFactory")

fun <T: Any> Retrofit.service(serviceClass: Class<T>): T = create(serviceClass)

fun <T: Any> Retrofit.service(serviceClass: KClass<T>): T = create(serviceClass.java)

inline fun <reified T: Any> Retrofit.service(): T = create(T::class.java)
