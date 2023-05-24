pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

val PROJECT_NAME = "bluetape4k"

rootProject.name = "$PROJECT_NAME-root"

includeModules(PROJECT_NAME, false)

includeModules("aws")
includeModules("data")
includeModules("infra")
includeModules("io")
includeModules("quarkus")
includeModules("spring")
includeModules("test")
includeModules("tokenizer")
includeModules("utils")
includeModules("vertx")

// Examples (library style examples)
includeModules("examples", false)
// Workshop (application style examples)

includeModules("workshop/docker", false, false)
includeModules("workshop/graphql", false, false)
includeModules("workshop/quarkus", false, false)
includeModules("workshop/spring-boot", false, false)
includeModules("workshop/spring-data", false, false)
includeModules("workshop/spring-security", false, false)
includeModules("workshop/vertx", false, false)

fun includeModules(baseDir: String, withProjectName: Boolean = true, withBaseDir: Boolean = true) {
    files("$rootDir/$baseDir").files
        .filter { it.isDirectory }
        .forEach { moduleDir ->
            moduleDir.listFiles()
                ?.filter { it.isDirectory }
                ?.forEach { dir ->
                    val basePath = baseDir.replace("/", "-")
                    val projectName = when {
                        !withProjectName && !withBaseDir -> dir.name
                        withProjectName                  -> PROJECT_NAME + "-" + basePath + "-" + dir.name
                        else                             -> basePath + "-" + dir.name
                    }
                    // println("include modules: $projectName")

                    include(projectName)
                    project(":$projectName").projectDir = dir
                }
        }
}
