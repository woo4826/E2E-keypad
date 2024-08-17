plugins {
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    id("org.springframework.boot") version "3.2.5"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "kr.bob"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    testImplementation(kotlin("test"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.7.3")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.7.3")
//    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions:1.2.2")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.6.0")
    implementation("commons-codec:commons-codec:1.17.1")
    runtimeOnly("com.h2database:h2")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}