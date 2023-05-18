package io.bluetape4k.workshop.graphql.dgs.service

import io.bluetape4k.workshop.graphql.dgs.generated.types.Show

interface ShowService {

    suspend fun shows(): List<Show>

    suspend fun shows(titleFilter: String): List<Show>

}
