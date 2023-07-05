# docker compose-demo

이 예제는 `Testcontainers`의 `DockerComposeContainer` 사용하여 `docker-compose.yml` 파일로부터 복수의 컨테이너를 실행하는 방법을 보여줍니다.

## 참고

* [Docker Compose Module](https://www.testcontainers.org/modules/docker_compose/)
* [How to run Docker Compose with Testcontainers](https://codeal.medium.com/how-to-run-docker-compose-with-testcontainers-7d1ba73afeeb)
* [Simple and Powerful Integration Tests with Gradle and Docker-Compose](https://codeal.medium.com/guide-simple-and-powerful-integration-tests-with-gradle-and-docker-compose-7a27bd06a0cd)

## Throuble Shooting

### Q. `Container startup failed for image alpine/socat:1.7.4.3-r0` 예외 발생 시

[alpine/socat container pinned at old version lacking arm64 platform](https://github.com/testcontainers/testcontainers-java/issues/5279)
를 참고해서, `~/.testcontainers.properties` 파일에 `socat.container.image=alpine/socat:latest` 를 추가하면 됩니다.

```shell
$grep socat ~/.testcontainers.properties
socat.container.image=alpine/socat:latest
```
