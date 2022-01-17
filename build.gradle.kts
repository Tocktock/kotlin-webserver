import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
}

group="org.example"
version="1.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.0")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.0")
//    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.6.0")
    implementation (group= "ch.qos.logback", name= "logback-classic", version= "1.3.0-alpha5")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.13.1")
    implementation("org.json:json:20211205")
    testImplementation("org.jetbrains.kotlin", "kotlin-test-junit5", "1.6.10")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
