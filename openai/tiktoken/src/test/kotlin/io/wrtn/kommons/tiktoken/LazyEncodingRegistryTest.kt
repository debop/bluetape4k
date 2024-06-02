package io.bluetape4k.tiktoken

class LazyEncodingRegistryTest: AbstractEncodingRegistryTest<LazyEncodingRegistry>() {

    override val registry = LazyEncodingRegistry()
    override val initializer: (LazyEncodingRegistry) -> Unit = { }

    // 단독으로 실행해야 하는 테스트라 주석화하였음
    //    @Suppress("UNCHECKED_CAST")
    //    @Test
    //    fun `initialize with empty encoding`() {
    //        val field = AbstractEncodingRegistry::class.java.getDeclaredField("encodings")
    //        field.isAccessible = true
    //
    //        val encodings = field.get(registry) as ConcurrentHashMap<String, Encoding>
    //        encodings.isEmpty().shouldBeTrue()
    //    }
}
