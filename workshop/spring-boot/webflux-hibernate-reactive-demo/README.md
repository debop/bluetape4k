# Spring Webflux + Hibernate Reactive Demo

Spring Webflux 에서 Hibernate Reactive를 사용하는 예제입니다.

현재는 bootRun 등 Application을 직접 실행하면 예외가 발생합니다.
테스트만을 위해 H2 메모리 DB를 사용하므로, Application을 실행하면 메모리 DB가 매번 바뀌어서 실행에서 예외가 발생합니다. MySQL 등 영구저장소를 사용하면 제대로 실행됩니다.
H2 메모리 DB는 테스트 실행 시에만 사용해야 합니다.

## 참고

Quarkus Framework 에서 Hibernate Reactive 를 사용하는 예제는
[hibernate-reactive-panache-demo](../../quarkus/hibernate-reactive-panache-demo/README.md) 를 참고하세요.
