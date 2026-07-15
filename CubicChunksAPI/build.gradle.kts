import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

plugins {
    java
    `maven-publish`
    idea
    id("com.github.hierynomus.license") version "0.16.1"
    id("com.gtnewhorizons.retrofuturagradle") version "2.0.2"
}

minecraft {
    mcVersion.set("1.12.2")
    mcpMappingChannel.set("stable")
    mcpMappingVersion.set("39")
}

val licenseYear: String by project
val projectName: String by project
val doRelease: String by project
val modVersion: String by project

logger.error(project.gradle.gradleVersion)
group = "io.github.opencubicchunks"
version = modVersion

base {
    archivesName.set("CubicChunksAPI")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

repositories {
    mavenLocal()
    mavenCentral()
}

idea {
    module.apply {
        inheritOutputDirs = true
    }
    module.isDownloadJavadoc = true
    module.isDownloadSources = true
}

fun configureManifest(manifest: Manifest) {
    manifest.attributes(
        "Specification-Title" to project.name,
        "Specification-Version" to project.version,
        "Specification-Vendor" to "OpenCubicChunks",
        "Implementation-Title" to "${project.group}.${project.name.lowercase(Locale.ROOT).replace(' ', '_')}",
        "Implementation-Version" to project.version,
        "Implementation-Vendor" to "OpenCubicChunks",
        "Implementation-Timestamp" to DateTimeFormatter.ISO_INSTANT.format(Instant.now())
    )
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("api")
    from(sourceSets.main.get().output)
    exclude("LICENSE.txt")
    configureManifest(manifest)
}

tasks.register<Jar>("deobfApiJar") {
    archiveClassifier.set("api-dev")
    from(sourceSets.main.get().output)
    exclude("LICENSE.txt")
    configureManifest(manifest)
}

tasks.register<Jar>("deobfSrcJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().java.srcDirs)
}

tasks.matching { it.name in listOf("licenseMcLauncher", "licensePatchedMc", "licenseInjectedTags", "licenseInjectedInterfaces", "licenseIdeVirtualMain") }.configureEach {
    enabled = false
}

license {
    ext["project"] = projectName
    ext["year"] = licenseYear
    exclude("**/*.info")
    exclude("**/package-info.java")
    exclude("**/*.json")
    exclude("**/*.xml")
    exclude("assets/*")
    header = file("HEADER.txt")
    ignoreFailures = false
    strictCheck = true
    mapping(mapOf("java" to "SLASHSTAR_STYLE"))
}
