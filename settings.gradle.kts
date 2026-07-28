rootProject.name = "ianua-silvae"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

buildCache {
    local {
        isEnabled = true
        directory = file("$rootDir/.gradle/build-cache")
    }
}
