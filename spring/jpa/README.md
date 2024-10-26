# Module bluetape4k-spring-jpa

## 주요 기능

- **StatelessSession 생성**: 트랜잭션 환경에서 `StatelessSession`을 생성하고 관리합니다.
- **트랜잭션 동기화**: 트랜잭션과 `StatelessSession`을 동기화하여 자동으로 세션을 닫고 자원을 해제합니다.

## 설치

Gradle을 사용하여 `bluetape4k-core` 모듈을 프로젝트에 추가할 수 있습니다.

```kotlin
dependencies {
    api(project(":bluetape4k-core"))
}
```

## StatelessSessionFactoryBean

`StatelessSessionFactoryBean`은 Hibernate의 `StatelessSession`을 Spring Data JPA의 트랜잭션 환경에서 사용할 수 있도록 해주는 Factory Bean입니다.

## 사용법

### StatelessSessionFactoryBean 설정

Spring 설정 파일에 `StatelessSessionFactoryBean`을 추가합니다.

```kotlin
import io.bluetape4k.spring.jpa.stateless.StatelessSessionFactoryBean
import org.hibernate.SessionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {

    @Bean
    fun statelessSessionFactoryBean(sessionFactory: SessionFactory): StatelessSessionFactoryBean {
        return StatelessSessionFactoryBean(sessionFactory)
    }
}
```

### StatelessSession 사용 예제

`StatelessSession`을 사용하여 대량의 데이터를 삽입하는 예제입니다.

```kotlin
import io.bluetape4k.spring.jpa.stateless.StatelessSessionFactoryBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StatelessService {

    @PersistentContext
    private val entityManager: EntityManager = unitialized()

    @Transactional
    fun insertData(entities: List<MyEntity>) {
        entityManager.withStatelessSession { stateless ->
            entities.forEach { entity ->
                stateless.insert(entity)
            }
        }
    }
}
```
