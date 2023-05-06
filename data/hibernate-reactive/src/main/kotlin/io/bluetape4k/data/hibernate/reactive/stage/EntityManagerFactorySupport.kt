package io.bluetape4k.data.hibernate.reactive.stage

import org.hibernate.reactive.stage.Stage
import javax.persistence.EntityManagerFactory


fun EntityManagerFactory.asStageSessionFactory(): Stage.SessionFactory {
    return unwrap(Stage.SessionFactory::class.java)
}
