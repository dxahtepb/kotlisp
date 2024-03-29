/*
 * This file was generated by the Gradle 'init' task.
 */

plugins {
    java
    kotlin("jvm") version "1.8.20"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.apache.commons:commons-text:1.7")
    testImplementation("junit:junit:4.12")
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}

group = "com.chausov"
version = "1.0-SNAPSHOT"
description = "Kotlisp"

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.chausov.kotlisp.run.MainKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}