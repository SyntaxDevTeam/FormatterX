plugins {
    kotlin("jvm") version "2.1.10"
    id("com.gradleup.shadow") version "9.0.0-beta7"
}

group = "pl.syntaxdevteam.formatter"
version = "1.0-SNAPSHOT"
description = "The intelligent chat formatting plugin with minimessages support!"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://repo.extendedclip.com/releases/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public")
}

dependencies {
    compileOnly("dev.folia:folia-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("org.eclipse.aether:aether-api:1.1.0")
    compileOnly("org.yaml:snakeyaml:2.3")
    compileOnly("com.google.code.gson:gson:2.12.1")
    compileOnly("net.kyori:adventure-text-serializer-legacy:4.18.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.18.0")
    compileOnly("net.kyori:adventure-text-serializer-gson:4.18.0")
    compileOnly("net.kyori:adventure-text-serializer-plain:4.18.0")
    compileOnly("net.kyori:adventure-text-serializer-ansi:4.18.0")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.github.milkbowl:VaultAPI:1.7.1")
    compileOnly("net.milkbowl.vault:VaultUnlockedAPI:2.9")
}

val targetJavaVersion = 21
kotlin {
    jvmToolchain(targetJavaVersion)
}

tasks.build {
    dependsOn("shadowJar")
}

tasks.processResources {
    val props = mapOf("version" to version, "description" to description)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("paper-plugin.yml") {
        expand(props)
    }
}
