# Module bluetape4k-openai-tiktoken

Kotlin tokenizer library designed for use with OpenAI models.

```kotlin
val registry = Encodings.newDefaultEncodingRegistry();
val enc = registry.getEncoding(EncodingType.CL100K_BASE);
enc.decode(enc.encode("hello world")) shouldBeEqualTo "hello world"


// Or get the tokenizer corresponding to a specific OpenAI model
enc = registry.getEncodingForModel(ModelType.TEXT_EMBEDDING_ADA_002);
```

## 🤖 Features

✅ Implements encoding and decoding via `r50k_base`, `p50k_base`, `p50k_edit` and `cl100k_base`

✅ Easy-to-use API

✅ Easy extensibility for custom encoding algorithms

✅ Supports Java 17 and above

✅ Fast and efficient performance
