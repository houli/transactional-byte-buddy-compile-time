plugins {
    java
}

group = "io.github.houli"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.bytebuddy:byte-buddy:1.14.17")
    implementation(project(":lib"))
}
