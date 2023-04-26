pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

rootProject.name = "bluetape4k"

include(rootProject.name + "-bom")
include(rootProject.name + "-core")

includeModules("data")
includeModules("io")
includeModules("kotlinx")
includeModules("quarkus")
includeModules("spring")
includeModules("test")
includeModules("tokenizer")
includeModules("utils")
includeModules("vertx")

// for example
includeModules("examples")

fun includeModules(baseDir: String) {
    files("$rootDir/$baseDir").files
        .filter { it.isDirectory }
        .forEach { moduleDir ->
            moduleDir.listFiles()
                ?.filter { it.isDirectory }
                ?.forEach { dir ->
                    val projectName = when {
                        baseDir.contains("examples") -> baseDir + "-" + dir.name
                        else -> rootProject.name + "-" + baseDir + "-" + dir.name
                    }
                    // println("include modules: $projectName")

                    include(projectName)
                    project(":$projectName").projectDir = dir
                }
        }
}
