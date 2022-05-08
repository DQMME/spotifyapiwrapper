import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktorVersion: String by project

plugins {
    kotlin("jvm") version "1.7.0-Beta"
    kotlin("plugin.serialization") version "1.7.0-Beta"
    application
}

group = "de.dqmme"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

application {
    mainClass.set("de.dqmme.spotifyapiwrapper.MainKt")
}