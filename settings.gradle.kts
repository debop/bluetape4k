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
includeModules("spring")
includeModules("test")
includeModules("tokenizer")
includeModules("utils")
includeModules("vertx")

// for example
includeModules("examples", false)

fun includeModules(baseDir: String, withProjectName: Boolean = true) {
    files("$rootDir/$baseDir").files
        .filter { it.isDirectory }
        .forEach { moduleDir ->
            moduleDir.listFiles()
                ?.filter { it.isDirectory }
                ?.forEach { dir ->
                    val projectName = when {
                        withProjectName -> PROJECT_NAME + "-" + baseDir + "-" + dir.name
                        else -> baseDir + "-" + dir.name
                    }
                    // println("include modules: $projectName")

                    include(projectName)
                    project(":$projectName").projectDir = dir
                }
        }
}
