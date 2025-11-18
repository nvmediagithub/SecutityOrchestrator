plugins {
    java
}

group = "org.example"
version = "0.0.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    compileOnly(libs.jackson.databind)
    compileOnly(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.jakarta.persistence.api)

    // Testing
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.mockito.core)
    testImplementation(libs.junit.jupiter)
}