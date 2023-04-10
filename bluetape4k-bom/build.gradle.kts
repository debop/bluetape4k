plugins {
    `java-platform`
    `maven-publish`
}

dependencies {
    constraints {
        rootProject.subprojects {
            if (name != "bluetape4k-bom") {
                api(this)
            }
        }
    }
}

val nexusHost: String by project
val nexusDeployUser: String by project
val nexusDeployPassword: String by project

publishing {
    publications {
        register("Maven", MavenPublication::class) {
            from(components["javaPlatform"])
        }
    }
    repositories {
//        maven {
//            val suffix = if (project.version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"
//            url = uri("https://$nexusHost/repository/maven-$suffix")
//            // <3>
//            credentials {
//                username = nexusDeployUser
//                password = nexusDeployPassword
//            }
//        }
        mavenLocal()
    }
}
