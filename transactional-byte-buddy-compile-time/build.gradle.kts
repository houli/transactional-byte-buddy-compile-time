import io.github.houli.plugin.TransactionalByteBuddyPlugin

plugins {
    java
    application
    id("net.bytebuddy.byte-buddy-gradle-plugin") version "1.14.17"
}

group = "io.github.houli"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation(project(":lib"))
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

byteBuddy {
    transformation {
        plugin = TransactionalByteBuddyPlugin::class.java
    }
}

application {
    mainClass = "io.github.houli.Application"
}
