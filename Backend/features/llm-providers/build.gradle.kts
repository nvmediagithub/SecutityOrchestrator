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
    compileOnly(libs.jackson.databind)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation(libs.httpclient)
    implementation(libs.okhttp)

    testImplementation(libs.spring.boot.starter.test)
}