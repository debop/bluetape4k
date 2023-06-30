package io.bluetape4k.openai.clients.retrofit2

import io.bluetape4k.openai.clients.retrofit2.sync.AudioSyncApi
import io.bluetape4k.openai.clients.retrofit2.sync.ChatSyncApi
import io.bluetape4k.openai.clients.retrofit2.sync.CompletionSyncApi
import io.bluetape4k.openai.clients.retrofit2.sync.EditSyncApi
import io.bluetape4k.openai.clients.retrofit2.sync.EmbeddingSyncApi
import io.bluetape4k.openai.clients.retrofit2.sync.FileSyncApi
import io.bluetape4k.openai.clients.retrofit2.sync.FineTuneSyncApi
import io.bluetape4k.openai.clients.retrofit2.sync.ImageSyncApi
import io.bluetape4k.openai.clients.retrofit2.sync.ModelSyncApi
import io.bluetape4k.openai.clients.retrofit2.sync.ModerationSyncApi

interface OpenAISyncApi:
    AudioSyncApi,
    ChatSyncApi,
    CompletionSyncApi,
    EditSyncApi,
    EmbeddingSyncApi,
    FileSyncApi,
    FineTuneSyncApi,
    ImageSyncApi,
    ModelSyncApi,
    ModerationSyncApi
