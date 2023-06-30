package io.bluetape4k.openai.clients.retrofit2

interface OpenAIApi:
    OpenAISyncApi,
    OpenAICoroutineApi,
    OpenAIReactiveApi
