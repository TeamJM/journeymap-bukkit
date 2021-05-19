import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`

    kotlin("jvm") version "1.5.0"

    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("io.gitlab.arturbosch.detekt") version "1.15.0"
}

group = "info.journeymap.journeymap-bukkit"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    explicitApi()
}

repositories {
    mavenCentral()

    maven {
        name = "Paper"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        name = "KotlinX"
        url = uri("https://dl.bintray.com/kotlin/kotlinx/")
    }
}

dependencies {
    detektPlugins(group = "io.gitlab.arturbosch.detekt", name = "detekt-formatting", version = "1.15.0")

    implementation(group = "org.jetbrains.kotlin", name = "kotlin-stdlib-jdk8")

    compileOnly(group = "org.bukkit", name = "bukkit", version = "1.14.4-R0.1-SNAPSHOT")
    testImplementation(group = "junit", name = "junit", version = "4.12")
}

detekt {
    buildUponDefaultConfig = true
    config = files("./detekt.yml")

    autoCorrect = true
}

tasks.withType<KotlinCompile> {
    // Current LTS version of Java
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

tasks.withType<ShadowJar> {
    relocate("org.jetbrains.kotlin", "info.journeymap.shaded.org.jetbrains.kotlin")
    relocate("kotlin", "info.journeymap.shaded.kotlin")
}

val sourcesJar = task("sourceJar", Jar::class) {
    dependsOn(tasks["classes"])
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

publishing {
    repositories {
        maven {
            name = "JourneyMap"
            url = uri("https://jm.gserv.me/repository/maven-snapshots/")

            credentials {
                username = project.findProperty("journeymap.user") as String?
                    ?: System.getenv("JOURNEYMAP_USER")

                password = project.findProperty("journeymap.password") as String?
                    ?: System.getenv("JOURNEYMAP_PASSWORD")
            }

            version = project.version
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components.getByName("java"))

            artifact(sourcesJar)
        }
    }
}

tasks.build {
    this.finalizedBy(sourcesJar, tasks.getByName("shadowJar"))
}
