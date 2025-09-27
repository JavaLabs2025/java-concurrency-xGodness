plugins {
    id("java")
    id("io.freefair.lombok") version "9.0.0-rc2"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

group = "org.labs"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("org.apache.logging.log4j:log4j-api:2.25.1")
    implementation("org.apache.logging.log4j:log4j-core:2.25.1")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.25.1")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}