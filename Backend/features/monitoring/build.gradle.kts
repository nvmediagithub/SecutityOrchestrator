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
    implementation(libs.spring.boot.starter.websocket)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.jackson.databind)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    testImplementation(libs.spring.boot.starter.test)
}