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
    compileOnly(libs.spring.boot.starter.web)
    compileOnly(libs.spring.boot.starter.validation)
    compileOnly(libs.jackson.databind)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // OpenAPI parsing and validation
    implementation("io.swagger.parser.v3:swagger-parser:2.1.19")
    implementation("io.swagger.core.v3:swagger-core:2.2.19")
    implementation("com.github.java-json-tools:json-schema-validator:2.2.14")
    implementation(libs.slf4j.api)

    testImplementation(libs.spring.boot.starter.test)
}