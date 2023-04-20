package io.bluetape4k.data.hibernate.reactive.stage

import javax.persistence.EntityManagerFactory
import org.hibernate.reactive.stage.Stage


fun EntityManagerFactory.asStageSessionFactory(): Stage.SessionFactory {
    return unwrap(Stage.SessionFactory::class.java)
}
