# Chaos Monkey + Spring Boot 2 Demo

원본: [chaos-monkey-springboot](https://github.com/vaquarkhan/chaos-monkey-springboot)

## 개요

Chaos Monkey for Spring Boot 에 대한 예제입니다.

## Chaos Monkey with Springboot

----------------------------------

## PRINCIPLES OF CHAOS ENGINEERING

- https://principlesofchaos.org/?lang=ENcontent

Chaos Monkey the solution, based on the idea behind Nelflix's tool, designed to test Spring Boot applications.
There are two required steps for enabling Chaos Monkey for a Spring Boot application.

----------------------------------

### First, let's add the library chaos-monkey-spring-boot to the project's dependencies.

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>chaos-monkey-spring-boot</artifactId>
    <version>2.2.0</version>
</dependency>
```

		
----------------------------------

### Then, we should activate the profile chaos-monkey on application startup.

```
-  spring.profiles.active=chaos-monkey

- $ java -jar target/order-service-1.0-SNAPSHOT.jar --spring.profiles.active=chaos-monkey

- inside eclipse enable profile
``` 

## Enable Spring Boot Actuator Endpoints

```yaml
management:
    endpoint:
    chaosmonkey:
        enabled: true
        endpoints:
    web:
        exposure:
        include: health,info,chaosmonkey
```

or

#### End point

```properties
management.endpoint.chaosmonkey.enabled:true
management.endpoint.chaosmonkeyjmx.enabled=true
```

#### inlcude all endpoints

     management.endpoints.web.exposure.include=*

#### include specific endpoints

```properties
     management.endpoints.web.exposure.include=health,info,metrics,chaosmonkey
```

--------------------------

http://localhost:8080/students

http://localhost:8080/student?id=10001

--------------------------

### GET

http://localhost:8080/actuator/chaosmonkey

```json
{
  "chaosMonkeyProperties": {
    "enabled": true
  },
  "assaultProperties": {
    "level": 5,
    "latencyRangeStart": 10000,
    "latencyRangeEnd": 15000,
    "latencyActive": false,
    "exceptionsActive": false,
    "exception": {
      "type": null,
      "arguments": null
    },
    "killApplicationActive": false,
    "memoryActive": false,
    "memoryMillisecondsHoldFilledMemory": 90000,
    "memoryMillisecondsWaitNextIncrease": 1000,
    "memoryFillIncrementFraction": 0.15,
    "memoryFillTargetFraction": 0.25,
    "runtimeAssaultCronExpression": "OFF",
    "watchedCustomServices": null
  },
  "watcherProperties": {
    "controller": true,
    "restController": true,
    "service": true,
    "repository": true,
    "component": false
  }
}
```

### GET

http://localhost:8080/actuator/chaosmonkey/status

      Ready to be evil!

### POST

http://localhost:8080/actuator/chaosmonkey/enable

### POST

http://localhost:8080/actuator/chaosmonkey/disable

### GET

http://localhost:8080/actuator/chaosmonkey/watchers

```json
{
  "controller": true,
  "restController": false,
  "service": true,
  "repository": false,
  "component": false
}
```

### GET

http://localhost:8080/actuator/chaosmonkey/assaults

```json
{
  "level": 5,
  "latencyRangeStart": 10000,
  "latencyRangeEnd": 15000,
  "latencyActive": false,
  "exceptionsActive": false,
  "exception": {
    "type": null,
    "arguments": null
  },
  "killApplicationActive": false,
  "memoryActive": false,
  "memoryMillisecondsHoldFilledMemory": 90000,
  "memoryMillisecondsWaitNextIncrease": 1000,
  "memoryFillIncrementFraction": 0.15,
  "memoryFillTargetFraction": 0.25,
  "runtimeAssaultCronExpression": "OFF",
  "watchedCustomServices": null
}
```

## Exception

### POST

http://localhost:8080/actuator/chaosmonkey/assaults

```json
{
  "level": 3,
  "latencyRangeStart": 20000,
  "latencyRangeEnd": 50000,
  "latencyActive": false,
  "exceptionsActive": true,
  "killApplicationActive": false,
  "exception": {
    "type": "java.lang.IllegalArgumentException",
    "arguments": [
      {
        "className": "java.lang.String",
        "value": "custom illegal argument exception"
      }
    ]
  }
}
```

## Latency

### POST

http://localhost:8080/actuator/chaosmonkey/assaults

```json
{
  "level": 1,
  "latencyRangeStart": 20000,
  "latencyRangeEnd": 50000,
  "latencyActive": true,
  "exceptionsActive": false,
  "killApplicationActive": false,
  "restartApplicationActive": false
}
```

## Method test

You can customize the behavior of all watchers using the property watchedCustomServices and decide which classes and
public methods should be attacked.
If no signatures are stored, all classes and public methods, recognized by the watchers are attacked by default.You can
either maintain the list in your
application properties or adjust it at runtime using the Spring Boot Actuator Endpoint.

### POST

http://localhost:8080/actuator/chaosmonkey/assaults \

```json
{
  "level": 1,
  "latencyRangeStart": 20000,
  "latencyRangeEnd": 50000,
  "latencyActive": true,
  "exceptionsActive": false,
  "killApplicationActive": false,
  "restartApplicationActive": false,
  "watchedCustomServices": [
    "com.khan.vaquar.demo.controller.StudentController.findAll"
  ]
}
```

After adding assult in findAll method you can see only latancy inside of findAll method

`http://localhost:8080/students`

Other methods are working without issue

`http://localhost:8080/student?id=10001`

Same logic applicable to exceptions :

```json
{
  "level": 1,
  "latencyRangeStart": 20000,
  "latencyRangeEnd": 50000,
  "latencyActive": false,
  "exceptionsActive": true,
  "exception": {
    "type": "java.lang.IllegalArgumentException",
    "arguments": [
      {
        "className": "java.lang.String",
        "value": "custom illegal argument exception"
      }
    ]
  },
  "killApplicationActive": false,
  "restartApplicationActive": false,
  "watchedCustomServices": [
    "com.khan.vaquar.demo.controller.StudentController.findAll"
  ]
}
```

After adding assult in findAll method you can see only exception inside of findAll method

`http://localhost:8080/students`

Other methods are working without issue

`http://localhost:8080/student?id=10001`

### POST

http://localhost:8080/actuator/chaosmonkey/assaults \

```json
{
  "level": 1,
  "latencyRangeStart": 1000,
  "latencyRangeEnd": 3000,
  "latencyActive": true,
  "exceptionsActive": false,
  "killApplicationActive": false,
  "memoryActive": false,
  "memoryMillisecondsHoldFilledMemory": 90000,
  "memoryMillisecondsWaitNextIncrease": 1000,
  "memoryFillIncrementFraction": 0.15,
  "memoryFillTargetFraction": 0.25,
  "runtimeAssaultCronExpression": "OFF",
  "watchedCustomServices": null
}
```

or

```json
{
  "level": 2,
  "latencyRangeStart": 1000,
  "latencyRangeEnd": 3000,
  "latencyActive": true,
  "exceptionsActive": false,
  "killApplicationActive": false,
  "memoryActive": true,
  "memoryMillisecondsHoldFilledMemory": 90000,
  "memoryMillisecondsWaitNextIncrease": 1000,
  "memoryFillIncrementFraction": 99.10,
  "memoryFillTargetFraction": 99.10,
  "runtimeAssaultCronExpression": "OFF",
  "watchedCustomServices": null
}
```

### POST

http://localhost:8080/actuator/chaosmonkey/assaults

```json
{
  "latencyRangeStart": 2000,
  "latencyRangeEnd": 5000,
  "latencyActive": true,
  "exceptionsActive": false,
  "killApplicationActive": false
}
```

### POST

http://localhost:8080/actuator/chaosmonkey/assaults

```json
{
  "latencyActive": false,
  "exceptionsActive": true,
  "killApplicationActive": false
}
```

### POST

http://localhost:8080/actuator/chaosmonkey/assaults

```json
{
  "latencyActive": false,
  "exceptionsActive": false,
  "killApplicationActive": true
}
```

```json
{
  "chaosMonkeyProperties": {
    "enabled": true
  },
  "assaultProperties": {
    "level": 3,
    "latencyRangeStart": 1000,
    "latencyRangeEnd": 3000,
    "latencyActive": true,
    "exceptionsActive": false,
    "killApplicationActive": false,
    "watchedCustomServices": []
  },
  "watcherProperties": {
    "controller": true,
    "restController": false,
    "service": true,
    "repository": false,
    "component": false
  }
}
```

-----------------------------------

## What is Chaos Eng -introductions including how to start your first chaos experiment:

https://www.gremlin.com/community/tutorials/chaos-engineering-the-history-principles-and-practice/

## Good summary of people, tools, companies doing chaos experiments:

https://coggle.it/diagram/WiKceGDAwgABrmyv/t/chaos-engineeringcompanies%2C-people%2C-tools-practices/0a2d4968c94723e48e1256e67df51d0f4217027143924b23517832f53c536e62

## Tools:

ChaosMonkey for SpringBoot: https://chaostoolkit.org/. Very easy to follow instructions. Easy
to turn on/off using Spring profile.

Spinnaker: https://www.spinnaker.io/. Netflix Chaos Monkey does not support deployments that are managed by anything
other than Spinnaker. That makes it pretty hard to use Chaos Monkey from Netflix.

Chaos Toolkit - https://chaostoolkit.org/. This tool is particularly helpful to my situation
since my applications are deployed in Cloud Foundry and this tool has a CloudFoundry extension. Pretty elaborate, but
easy to follow instructions. My preferred tool so far.

Chaos Lemur - https://content.pivotal.io/blog/chaos-lemur-testing-high-availability-on-pivotal-cloud-foundry. This tool
has promise but network admin won't share AWS credentials for me to muck with Pivotal cells.

Gramlin -https://www.gremlin.com/

- https://www.youtube.com/watch?v=-smx0-qeurw
- https://www.youtube.com/embed/cefJd2v037U
- https://netflix.github.io/chaosmonkey/
- https://chaostoolkit.org/
- https://codecentric.github.io/chaos-monkey-spring-boot/

---------------------------
