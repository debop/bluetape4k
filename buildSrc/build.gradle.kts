import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

repositories {
    mavenCentral()
    google()
}

plugins {
    `kotlin-dsl`
}

kotlin {
    compilerOptions {
        languageVersion.set(KotlinVersion.KOTLIN_2_0)
        apiVersion.set(KotlinVersion.KOTLIN_2_0)
    }
}
