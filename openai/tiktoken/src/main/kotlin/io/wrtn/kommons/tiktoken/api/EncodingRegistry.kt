package io.bluetape4k.tiktoken.api

/**
 * 인코딩 레지스트리는 사용자 정의 인코딩을 등록하고 이름과 유형별로 인코딩을 검색하는 데 사용됩니다.
 * 인코딩을 검색하는 데 사용됩니다. 기본적으로 지원되는 인코딩은 자동으로 등록됩니다.
 */
interface EncodingRegistry {

    /**
     * Returns the encoding with the given name, if it exists. Otherwise, returns an empty Optional.
     * Prefer using {@link #getEncoding(EncodingType)} or {@link #getEncodingForModel(ModelType)} for
     * built-in encodings.
     *
     * @param encodingName the name of the encoding
     * @return the encoding, if it exists
     */
    fun getEncoding(encodingName: String): Encoding?

    /**
     * Returns the encoding with the given type.
     *
     * @param encodingType the type of the encoding
     * @return the encoding
     */
    fun getEncoding(encodingType: EncodingType): Encoding

    /**
     * Returns the encoding that is used for the given model type, if it exists. Otherwise, returns an
     * empty Optional. Prefer using {@link #getEncodingForModel(ModelType)} for built-in encodings.
     * <p>
     * Note that you can use this method to retrieve the correct encodings for snapshots of models, for
     * example "gpt-4-0314" or "gpt-3.5-turbo-0301".
     *
     * @param modelName the name of the model to get the encoding for
     * @return the encoding, if it exists
     */
    fun getEncodingForModel(modelName: String): Encoding?

    /**
     * Returns the encoding that is used for the given model type.
     *
     * @param modelType the model type
     * @return the encoding
     */
    fun getEncodingForModel(modelType: ModelType): Encoding

    /**
     * Registers a new byte pair encoding with the given name. The encoding must be thread-safe.
     *
     * @param parameters the parameters for the encoding
     * @return the registry for method chaining
     * @see GptBytePairEncodingParams
     * @throws IllegalArgumentException if the encoding name is already registered
     */
    fun registerGptBytePairEncoding(parameters: GptBytePairEncodingParams): EncodingRegistry

    /**
     * Registers a new custom encoding with the given name. The encoding must be thread-safe.
     *
     * @param encoding the encoding
     * @return the registry for method chaining
     * @throws IllegalArgumentException if the encoding name is already registered
     */
    fun registerCustomEncoding(encoding: Encoding): EncodingRegistry
}
