# OpenTelemetry for Spring Boot 3 MVC

Spring Boot 3 MVC Application에 대해 Opentelemetry 를 이용하여, Metrics, Tracing, Logging 을 추출하고,
이를 OpenTelemetry Collector를 이용하여, Prometheus, Zipkin 등의 서버로 OTLP로 전송하나느 예제입니다.

## 실행 방법

### 1. OpenTelemetry Collector 실행

OpenTelemetry Collector, Prometheus, Zipkin 등을 실행합니다.

```shell
$ ./otel-collector/docker-compose up
```

### 2. Spring Boot Application 을 실행합니다.

```shell
$ ./gradlew :otel-mvc-demo:bootRun
```

### 3. API 호출

```shell
$ curl http://localhost:8080/roll/dice?name=debop
```

### 4. Tracing 및 Metrics 확인

Go to [Jaeger Backend](http://localhost:16686/) or [Zipkin backend](http://localhost:9412) to see result
