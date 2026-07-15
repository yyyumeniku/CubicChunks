pluginManagement {
    repositories {
        maven {
            name = "GTNH Maven"
            setUrl("https://nexus.gtnewhorizons.com/repository/public/")
            mavenContent {
                includeGroup("com.gtnewhorizons")
                includeGroup("com.gtnewhorizons.retrofuturagradle")
            }
        }
        maven {
            name = "SpongePowered"
            setUrl("https://repo.spongepowered.org/repository/maven-public/")
            mavenContent {
                includeGroupByRegex("org\\.spongepowered.*")
            }
        }
        maven {
            name = "CleanroomMC"
            setUrl("https://maven.cleanroommc.com/")
            mavenContent {
                includeGroup("zone.rong")
            }
        }
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}
rootProject.name = "CubicChunks"
includeBuild("CubicChunksAPI") {
    dependencySubstitution {
        substitute(module("io.github.opencubicchunks:cubicchunks-api")).using(project(":"))
    }
}
