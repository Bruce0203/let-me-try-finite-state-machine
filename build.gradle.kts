val kotlin_version = "1.7.0"
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath("gradle.plugin.com.github.johnrengelman:shadow:7.1.2")
    }
}
plugins {
    kotlin("jvm") version "1.7.0"
    application
}
apply(plugin = "com.github.johnrengelman.shadow")
tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName.set("${rootProject.name}.jar")
}

val projectMainClass = "App"
tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
            "Main-Class" to projectMainClass
        ))
    }
}

application {
    mainClass.set(projectMainClass)
}


repositories {
    mavenCentral()
}

dependencies {
    api(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    api("org.jetbrains.kotlin:kotlin-stdlib:${kotlin_version}")
    api("org.jetbrains.kotlin:kotlin-reflect:${kotlin_version}")
    api("org.jetbrains.kotlin:kotlin-test:${kotlin_version}")
    testImplementation(kotlin("test"))

}

tasks.test {
    useJUnitPlatform()
}
