package io.bluetape4k.spring.retrofit2

import org.springframework.cloud.context.named.NamedContextFactory

class RetrofitClientContext:
    NamedContextFactory<RetrofitClientSpecification>(
        DefaultRetrofitClientConfiguration::class.java,
        "retrofit2",
        "retrofit2.client.name"
    )
