import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*
import org.gradle.api.tasks.bundling.Jar
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.language.jvm.tasks.ProcessResources

plugins {
    java
    `maven-publish`
    idea
    id("com.github.hierynomus.license") version "0.16.1"
    id("com.gradleup.shadow") version "9.4.0"
    id("com.gtnewhorizons.retrofuturagradle") version "2.0.2"
}

val licenseYear: String get() = project.findProperty("licenseYear")?.toString() ?: ""
val projectName: String get() = project.findProperty("projectName")?.toString() ?: ""
val doRelease: String get() = project.findProperty("doRelease")?.toString() ?: ""
val modVersion: String get() = project.findProperty("modVersion")?.toString() ?: ""

logger.error(project.gradle.gradleVersion)
group = "io.github.opencubicchunks"
version = modVersion

base {
    archivesName.set("CubicChunks")
}


minecraft {
    mcVersion.set("1.12.2")
    mcpMappingChannel.set("stable")
    mcpMappingVersion.set("39")
    applyMcDependencies.set(true)
}


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

sourceSets.main {
    ext["refMap"] = "cubicchunks.mixins.refmap.json"
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "SpigotMC"
        setUrl("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        mavenContent {
            includeGroupByRegex("(net\\.md-5|org\\.spigotmc|org\\.bukkit).*")
        }
    }
    maven {
        name = "SpongePowered"
        setUrl("https://repo.spongepowered.org/repository/maven-public/")
        mavenContent {
            includeGroup("org.spongepowered")
            includeGroup("io.github.opencubicchunks")
            snapshotsOnly()
        }
    }
    maven {
        name = "GTNH Nexus"
        setUrl("https://nexus.gtnewhorizons.com/content/repositories/snapshots/")
    }
    maven {
        name = "Forge Maven"
        setUrl("https://files.minecraftforge.net/maven/")
    }
    maven {
        name = "DaPorkchop Snapshots"
        setUrl("https://maven.daporkchop.net/snapshot/")
        mavenContent {
            includeGroup("com.flowpowered")
            includeGroup("io.github.opencubicchunks")
            snapshotsOnly()
        }
    }
    maven {
        name = "Minebench Maven"
        setUrl("https://repo.minebench.de/")
        mavenContent {
            includeGroup("com.flowpowered")
        }
    }
    maven {
        name = "CleanroomMC"
        setUrl("https://maven.cleanroommc.com/")
        mavenContent {
            includeGroup("zone.rong")
        }
    }
}

dependencies {
    implementation("com.google.guava:guava:32.0.1-jre")
    implementation("io.github.opencubicchunks:regionlib:0.78.0-SNAPSHOT")
    implementation("com.flowpowered:flow-noise:1.0.1-SNAPSHOT")
    implementation("io.github.opencubicchunks:cubicchunks-api:0.0.1000-SNAPSHOT")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    compileOnly("org.spongepowered:mixin:0.8.5")
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT") {
        exclude(group = "net.md-5", module = "bungeecord-chat")
    }
    runtimeOnly("zone.rong:mixinbooter:10.7")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest-junit:2.0.0.0")
    testImplementation("it.ozimov:java7-hamcrest-matchers:1.3.0")
    testImplementation("org.mockito:mockito-core:4.2.0")
    testImplementation("org.spongepowered:launchwrappertestsuite:1.0-SNAPSHOT")
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
        "Implementation-Timestamp" to DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
        "FMLCorePlugin" to "io.github.opencubicchunks.cubicchunks.core.asm.coremod.CubicChunksCoreMod",
        "TweakClass" to "org.spongepowered.asm.launch.MixinTweaker",
        "TweakOrder" to "0",
        "ForceLoadAsMod" to "true",
        "FMLCorePluginContainsFMLMod" to "true"
    )
}

tasks.test {
    systemProperty("lwts.tweaker", "cubicchunks.tweaker.MixinTweakerServer")
    jvmArgs("-Dmixin.debug.verbose=true", "-Dmixin.checks.interfaces=true", "-Dmixin.env.remapRefMap=true")
    testLogging {
        showStandardStreams = true
    }
}

tasks.named<Jar>("jar") {
    from(sourceSets.main.get().output)
    exclude("LICENSE.txt", "log4j2.xml")
    configureManifest(manifest)
    archiveClassifier.set("dev")
}

tasks.register<Jar>("deobfSourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().java.srcDirs)
}

tasks.named<ShadowJar>("shadowJar") {
    archiveClassifier.set("all")
    from(sourceSets.main.get().output)
    exclude("META-INF/MUMFREY*")
    exclude("log4j2.xml")
    exclude("LICENSE.txt")
    manifest {
        configureManifest(this)
    }
}

license {
    ext["project"] = projectName
    ext["year"] = licenseYear
    exclude("**/*.info")
    exclude("**/package-info.java")
    exclude("**/*.json")
    exclude("**/*.xml")
    exclude("assets/*")
    exclude("io/github/opencubicchunks/cubicchunks/core/server/chunkio/async/forge/*")
    exclude("io/github/opencubicchunks/cubicchunks/core/lighting/phosphor/*")
    header = file("HEADER.txt")
    ignoreFailures = false
    strictCheck = true
    mapping(mapOf("java" to "SLASHSTAR_STYLE"))
}

tasks.matching { it.name in listOf("licenseMcLauncher", "licensePatchedMc", "licenseInjectedTags", "licenseInjectedInterfaces", "licenseIdeVirtualMain") }.configureEach {
    enabled = false
}

tasks.named<ProcessResources>("processResources") {
    filesMatching("mcmod.info") {
        expand("mod_version" to project.version.toString())
    }
}

tasks.register<ShadowJar>("devShadowJar") {
    archiveClassifier.set("dev-all")
    from(sourceSets.main.get().output)
    configurations.add(project.configurations.named("runtimeClasspath").get())
    exclude("META-INF/MUMFREY*")
    exclude("log4j2.xml")
    exclude("LICENSE.txt")
    manifest {
        configureManifest(this)
    }
}

afterEvaluate {
    tasks.named("publish") {
        dependsOn(gradle.includedBuild("CubicChunksAPI").task(":publish"))
    }
}
