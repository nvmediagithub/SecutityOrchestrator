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
    implementation(project(":shared"))
    compileOnly(libs.spring.boot.starter)
    compileOnly(libs.spring.boot.starter.validation)
    compileOnly(libs.spring.boot.starter.data.jpa)
    compileOnly(libs.jackson.databind)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Camunda BPMN engine for parsing and validation
    implementation("org.camunda.bpm:camunda-engine:7.21.0")

    testImplementation(libs.spring.boot.starter.test)
}