import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
    mavenCentral()
    maven("https://repo.spring.io/milestone")
    maven("https://repo.spring.io/snapshot")
}

plugins {
    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.4"

    val kotlinVersion = "1.9.22"
    kotlin("kapt") version kotlinVersion
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
    kotlin("plugin.lombok") version kotlinVersion

    id("java")
    id("io.freefair.lombok") version "8.1.0"
}

group = "org.ailingo"
version = "0.0.1-SNAPSHOT"

val javaVersion = JavaVersion.VERSION_17

java {
    sourceCompatibility = javaVersion
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-rest")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // PostgreSQL
    runtimeOnly("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core")

    // ChatGPT
    implementation(platform("org.springframework.ai:spring-ai-bom:1.0.0-SNAPSHOT"))
    implementation("org.springframework.ai:spring-ai-spring-boot-autoconfigure")
    implementation("org.springframework.ai:spring-ai-openai")

    // Mapstruct
    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Lombok
    kapt("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Tests
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    val testContainers = "1.19.8"
    testImplementation("org.testcontainers:testcontainers:$testContainers")
    testImplementation(platform("org.testcontainers:testcontainers-bom:$testContainers"))
    testImplementation("org.testcontainers:postgresql:$testContainers")
    testImplementation("org.testcontainers:junit-jupiter:$testContainers")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // .Env files
    implementation("me.paulschwarz:spring-dotenv:4.0.0")

    // Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

configure<SourceSetContainer> {
    named("main") {
        java.srcDir("src/main/kotlin")
    }
}


kapt {
    keepJavacAnnotationProcessors = true
}

tasks {

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += "-Xjsr305=strict"
            jvmTarget = javaVersion.toString()
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }
}