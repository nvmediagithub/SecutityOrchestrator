plugins {
    java
    id("org.springframework.boot") version "3.3.5"
    id("io.spring.dependency-management") version "1.1.6"
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
    implementation(project(":features:analysis_pipeline"))
    implementation(project(":features:llm"))
    implementation(project(":features:bpmn"))
    implementation(project(":features:openapi_module"))
    implementation(project(":features:testdata"))
    implementation(project(":features:orchestration"))
    implementation(project(":features:monitoring"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("org.json:json:20231013")
    implementation("org.projectlombok:lombok")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")

    // Add logging dependencies
    implementation("org.slf4j:slf4j-api")
    implementation("ch.qos.logback:logback-classic")

    // HTTP Client for external API calls
    implementation("org.apache.httpcomponents:httpclient:4.5.14")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // H2 Database for development
    runtimeOnly("com.h2database:h2")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.mockito:mockito-core")
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")

    // Development tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}
