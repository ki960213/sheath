// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath("com.github.dcendents:android-maven-gradle-plugin:2.1")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.1")
    }
}

plugins {
    id("com.android.application") version "8.1.0" apply false
    id("com.android.library") version "8.1.0" apply false

    kotlin("android") version "1.7.10" apply false
    kotlin("jvm") version "1.7.10" apply false
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0" apply false
}

allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
}

tasks.register<Delete>("clean") {
    delete(project.layout.buildDirectory.asFile)
}
