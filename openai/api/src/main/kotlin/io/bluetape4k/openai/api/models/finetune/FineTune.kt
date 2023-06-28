package io.bluetape4k.openai.api.models.finetune

import io.bluetape4k.openai.api.models.Status
import io.bluetape4k.openai.api.models.file.File
import io.bluetape4k.openai.api.models.model.ModelId
import java.io.Serializable

data class FineTune(
    val id: FineTuneId,
    val model: ModelId,
    val createdAt: Long,
    val events: List<FineTuneEvent>? = null,
    val fineTuneModel: ModelId? = null,
    val hyperparams: HyperParameters? = null,
    val organizationId: String? = null,
    val resultFiles: List<File>? = null,
    val status: Status? = null,
    val validationFiles: List<File>? = null,
    val trainingFiles: List<File>? = null,
    val updatedAt: Long? = null,
) : Serializable
