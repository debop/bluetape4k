package io.bluetape4k.openai.clients.retrofit2

import io.bluetape4k.openai.clients.retrofit2.coroutines.OpenAICoroutineApi
import io.bluetape4k.openai.clients.retrofit2.reactive.OpenAIReactiveApi
import io.bluetape4k.openai.clients.retrofit2.sync.OpenAISyncApi

interface OpenAIApi:
    OpenAISyncApi,
    OpenAICoroutineApi,
    OpenAIReactiveApi
