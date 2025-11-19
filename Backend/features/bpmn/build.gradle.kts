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
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation(libs.jackson.databind)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Camunda BPMN engine for parsing and validation
    implementation("org.camunda.bpm:camunda-engine:7.21.0")

    // In-memory database for BPMN feature
    runtimeOnly("com.h2database:h2")

    testImplementation(libs.spring.boot.starter.test)
}
