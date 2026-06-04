plugins {
    kotlin("jvm") version "2.4.0-Beta1"
    id("com.gradleup.shadow") version "9.4.1"
    kotlin("plugin.serialization") version "2.4.0-Beta1"
    `maven-publish`
}

group = "dev.meluhdy"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    api("com.jeff-media:custom-block-data:2.2.4")
    api("com.jeff-media:MorePersistentDataTypes:2.4.0")
}

val targetJavaVersion = 25
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.shadowJar {
    relocate("com.jeff_media.customblockdata", "dev.meluhdy.libs.customblockdata")
    relocate("com.jeff_media.morepersistentdatatypes", "dev.meluhdy.libs.morepersistentdatatypes")
}

tasks.build {
    dependsOn("shadowJar")
    finalizedBy(tasks.publishToMavenLocal)
}

tasks.processResources {
    val props = mapOf("version" to version, "description" to description)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "melodia"
            version = project.version.toString()

            from(components["shadow"])
        }
    }
}