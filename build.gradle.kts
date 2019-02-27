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

    implementation("org.leadpony.justify", "justify", "0.12.0")
    implementation("org.glassfish", "javax.json", "1.1.4")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
}
