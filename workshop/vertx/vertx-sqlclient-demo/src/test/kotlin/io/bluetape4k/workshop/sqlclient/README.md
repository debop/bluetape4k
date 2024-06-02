## Mapping with Vert.x data objects

Vert SqlClient 의 RowMapper 를 사용하지 않고, Rows 를 바로 DTO 에 매핑할 수 있다
dataobject 폴더의 UserDataObject 구현을 참고

1. `io.vertx:vertx-codegen:4.3.1:processor` 를 dependency 에 추가한다

```
compileOnly(Libs.vertx_codegen)
kapt(Libs.vertx_codegen)
kaptTest(Libs.vertx_codegen)
```

2. module에 package-info.java 를 추가한다

[package-info.java](src/main/java/io/bluetape4k/workshop/sqlclient/package-info.java)

```java
@ModuleGen(name = "vertx-sqlclient-demo", groupPackage = "io.bluetape4k.workshop.sqlclient")
package io.bluetape4k.workshop.sqlclient;

import io.vertx.codegen.annotations.ModuleGen;
```

참고 자료 :
[Mapping with Vert.x data objects](https://vertx.io/docs/vertx-sql-client-templates/java/#_mapping_with_vert_x_data_objects)
