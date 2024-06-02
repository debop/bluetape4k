package io.bluetape4k.openai.client.model

import com.fasterxml.jackson.annotation.JsonProperty

abstract class BaseObject {

    @JsonProperty("id")
    var id: String? = null

    @JsonProperty("object")
    var objectType: String? = null

}
