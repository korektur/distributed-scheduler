import org.jetbrains.kotlin.config.AnalysisFlag.Flags.experimental
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.js.translate.context.Namer.kotlin

buildscript {
    var kotlinVersion: String by extra
    kotlinVersion = "1.2.41"


    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
    }
}


group = "com.korektur"
version = "1.0-SNAPSHOT"

apply {
    plugin("kotlin")
}

val kotlinVersion: String by extra
val junitVersion = "5.2.0"
val slf4jVersion = "1.7.25"
val mockitoVersion = "2.18.3"
val mockitoKotlinVersion = "1.5.0"
val kotlinCoroutinesVersion = "0.22.5"

plugins {
    java
    kotlin("jvm").version("1.2.41")
}

repositories {
    mavenCentral()
    maven("http://repository.jetbrains.com/all")
}

kotlin { // configure<org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension>
    experimental.coroutines = Coroutines.ENABLE
}

dependencies {
    compile(kotlin("stdlib-jdk8", kotlinVersion))
    compile("org.jetbrains.kotlinx", "kotlinx-coroutines-core", kotlinCoroutinesVersion)
    compile("org.slf4j", "slf4j-api", slf4jVersion)

    testCompile("org.slf4j", "slf4j-simple", slf4jVersion)
    testCompile("org.junit.jupiter", "junit-jupiter-api", junitVersion)
    testCompile("org.junit.jupiter", "junit-jupiter-params", junitVersion)
    testCompile("org.mockito", "mockito-core", mockitoVersion)
    testCompile("com.nhaarman","mockito-kotlin" ,mockitoKotlinVersion)

    testRuntime("org.junit.jupiter", "junit-jupiter-engine", junitVersion)
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}