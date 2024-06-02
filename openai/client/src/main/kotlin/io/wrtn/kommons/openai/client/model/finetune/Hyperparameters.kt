package io.bluetape4k.openai.client.model.finetune

import java.io.Serializable

// TODO: 왜 get/set 을 모두 써야 제대로 동작하는지? 다시 찾아봐야겠다 

data class Hyperparameters(
    val n_epochs: Int? = null,
): Serializable 
