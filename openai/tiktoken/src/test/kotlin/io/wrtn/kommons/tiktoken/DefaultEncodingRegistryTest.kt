package io.bluetape4k.tiktoken

class DefaultEncodingRegistryTest: AbstractEncodingRegistryTest<DefaultEncodingRegistry>() {

    override val registry: DefaultEncodingRegistry = DefaultEncodingRegistry()

    override val initializer: (DefaultEncodingRegistry) -> Unit = {
        it.initializeDefaultEncodings()
    }

}
