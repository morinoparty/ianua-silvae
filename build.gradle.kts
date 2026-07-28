import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    application
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.shadow)
}

val version: String by project
group = "party.morino"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.minestom)
    implementation(libs.schem)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.logback.classic)

    testImplementation(libs.bundles.junit)
}

kotlin {
    jvmToolchain(25)
}

application {
    mainClass.set("party.morino.ianuasilvae.MainKt")
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
            events("passed", "skipped", "failed")
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
    compileKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_25)
        compilerOptions.javaParameters = true
    }
    compileTestKotlin {
        compilerOptions.jvmTarget.set(JvmTarget.JVM_25)
    }
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }
    jar {
        // Keep the thin jar out of the way; the shadow jar takes the unclassified name.
        archiveClassifier.set("plain")
        manifest {
            attributes["Main-Class"] = "party.morino.ianuasilvae.MainKt"
        }
    }
    shadowJar {
        // Minestom relies on service loaders, so merged service files are required.
        mergeServiceFiles()
        archiveClassifier.set("")
    }
    build {
        dependsOn(shadowJar)
    }
}
