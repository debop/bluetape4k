# Module bluetape4k-hibernate-reactive

## FIXME

`hibernate-reactive` 최신 버전인 `1.1.9.Final` 은 `hibernate` 5.6.+ 를 사용하고 있고, `javax` 기반이다. (jakarta 기반이 아님)

그래서 현재 `hibernate 6.2+` 와 `hibernate-reactive 2.0.0-Beta2` 를 가지고 테스트 중인데, 초기화 중에 `SqlStatementLogger` 에서 예외가 발생하여 문제를
해결해야 한다 
