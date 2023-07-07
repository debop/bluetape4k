# Module bluetape4k-utils-multibase

다양한 진법으로 숫자나 문자열을 인코딩, 디코딩을 수행합니다.

## 사용 예

```kotlin
val data: ByteArray = ...
val encoded: String = Multibase.encode(Multibase.Base.Base58BTC, data)
val decoded = Multibase.decode(encoded)
decoded shouldBeEqualTo data
```
