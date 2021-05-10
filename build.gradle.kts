buildscript {
    repositories {
        mavenCentral()
        jcenter()
        gradlePluginPortal()
    }
}

repositories {
    mavenCentral()
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

plugins {
    application
    kotlin("jvm") version "1.4.0"
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson-xml:$http4kVersion")
    implementation("org.http4k:http4k-format-kotlinx-serialization:$http4kVersion")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jodatime:$exposedVersion")
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("com.zaxxer:HikariCP:$hikariCpVersion")
    implementation("io.github.config4k:config4k:$config4kVersion")

    testImplementation("com.h2database:h2:$h2Version")
    testImplementation("org.http4k:http4k-testing-approval:$http4kVersion")
    testImplementation("org.http4k:http4k-testing-hamkrest:$http4kVersion")
    testImplementation("org.http4k:http4k-testing-kotest:$http4kVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testImplementation("io.kotlintest:kotlintest-runner-junit5:$kotlinJunit")
}

tasks {
    withType(org.jetbrains.kotlin.gradle.tasks.KotlinCompile::class.java).all {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    test {
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
