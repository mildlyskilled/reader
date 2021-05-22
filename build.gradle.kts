buildscript {
    repositories {
        mavenCentral()
        jcenter()
        gradlePluginPortal()
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

group = "com.mildlyskilled.reader"

application {
    mainClass.set("ReaderKt")
}

val kotlinVersion: String by project
val http4kVersion: String by project
val h2Version: String by project
val exposedVersion: String by project
val postgresqlVersion: String by project
val hikariCpVersion: String by project
val flywayVersion: String by project
val junitVersion: String by project
val config4kVersion: String by project
val kotlinJunit: String by project
val jacksonDataTypeJoda: String by project
val auth0Version: String by project
val scryptVersion: String by project
val muLoggingVersion: String by project
val arrowVersion: String by project
val kotlinReflect: String by project

plugins {
    application
    kotlin("jvm") version "1.4.0"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinReflect")
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson-xml:$http4kVersion")
    implementation("org.http4k:http4k-format-kotlinx-serialization:$http4kVersion")
    implementation("org.http4k:http4k-client-okhttp:$http4kVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jodatime:$exposedVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")
    implementation("io.github.config4k:config4k:$config4kVersion")
    implementation("com.auth0:java-jwt:$auth0Version")
    implementation("com.lambdaworks:scrypt:$scryptVersion")
    implementation("io.github.microutils:kotlin-logging-jvm:$muLoggingVersion")
    implementation("io.arrow-kt:arrow-core:$arrowVersion")

    testImplementation("com.h2database:h2:$h2Version")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotlinJunit")
    testImplementation("io.kotest:kotest-assertions-core:$kotlinJunit")

}

tasks {
    withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    withType<Test> {
        useJUnitPlatform()
        maxParallelForks = 1
        jvmArgs = listOf("-Dlogback.configurationFile=resources/logback.local.xml")
        testLogging {
            events("passed", "skipped", "failed")
            showStackTraces = true
            showExceptions = true
            showStandardStreams = false
        }
    }

}
