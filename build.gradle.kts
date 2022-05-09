import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

val ktorVersion: String by project

plugins {
    kotlin("jvm") version "1.7.0-Beta"
    kotlin("plugin.serialization") version "1.7.0-Beta"
    application
    id("org.jetbrains.dokka") version "1.6.21"
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

tasks.withType<DokkaTask>().configureEach {
    dokkaSourceSets {
        named("main") {
            moduleName.set("Spotify API Wrapper")
            sourceLink {
                localDirectory.set(file("src/main/kotlin"))
                remoteUrl.set(URL("https://github.com/DQMME/spotifyapiwrapper/tree/master/src/main/kotlin"))
                remoteLineSuffix.set("#L")
            }
        }
    }
}