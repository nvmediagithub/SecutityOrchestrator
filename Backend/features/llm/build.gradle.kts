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
    implementation(project(":features:llm-providers"))
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.jackson.databind)
    implementation(libs.jackson.dataformat.yaml)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Add slf4j-api for logging
    implementation(libs.slf4j.api)

    testImplementation(libs.spring.boot.starter.test)
}
