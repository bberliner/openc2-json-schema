group = "com.brianberliner.openc2"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.21"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
