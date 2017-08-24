plugins {
    war
    `maven-publish`

    kotlin("jvm", "1.1.4-2")
    kotlin("plugin.spring", "1.1.4-2")
    kotlin("plugin.jpa", "1.1.4-2")

    id("se.inera.intyg.plugin.common") version "1.0.45"
    id("org.springframework.boot") version "1.5.6.RELEASE"
    id("org.ajoberstar.grgit") version "2.0.0"
}

group = "se.inera.intyg.srs"
version = System.getProperty("buildVersion") ?: "0.0.1-SNAPSHOT"

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions { jvmTarget = "1.8" }
}

val versionTask = task<se.inera.intyg.VersionPropertyFileTask>("createVersionPropertyFile")
tasks["war"].dependsOn(versionTask)

task<se.inera.intyg.TagReleaseTask>("tagRelease")

tasks.withType<Test> {
    exclude("**/*IT*")
}

task<Test>("restAssuredTest") {
    outputs.upToDateWhen { false }
    include("**/*IT*")
}

publishing {
    publications {
        create<MavenPublication>("warFile") {
            from(components["web"])
        }
    }
    repositories {
        maven {
            url = uri("https://build-inera.nordicmedtest.se/nexus/repository/releases/")
            credentials {
                username = System.getProperty("nexusUsername")
                password = System.getProperty("nexusPassword")
            }
        }
    }
}

repositories {
    mavenLocal()
    maven { url = uri("https://build-inera.nordicmedtest.se/nexus/repository/releases/") }
    mavenCentral()
}

dependencies {
    val kotlinVersion = "1.1.4-2"

    compile(kotlin("stdlib", kotlinVersion))
    compile(kotlin("reflect", kotlinVersion))

    compile("se.inera.intyg.clinicalprocess.healthcond.srs:intyg-clinicalprocess-healthcond-srs-schemas:0.0.4")
    compile("se.riv.itintegration.monitoring:itintegration-monitoring-schemas:1.0.0.4")
    compile("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-starter-jdbc:")
    compile("org.springframework.boot:spring-boot-starter-data-jpa")
    compile("org.springframework.boot:spring-boot-starter-test")
    compile("org.springframework.boot:spring-boot-starter-web-services")
    compile("org.springframework.boot:spring-boot-starter-actuator")
    compile("org.apache.cxf:cxf-spring-boot-starter-jaxws:3.1.11")
    compile("org.liquibase:liquibase-core:3.5.3")
    compile("org.apache.logging.log4j:log4j-api:2.7")
    compile("org.apache.logging.log4j:log4j-core:2.7")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.8.4")
    compile("org.nuiton.thirdparty:JRI:0.9-9")
    compile("org.jadira.usertype:usertype.extended:5.0.0.GA")
    compile("com.google.guava:guava:23.0")

    runtime("com.h2database:h2")
    runtime("mysql:mysql-connector-java")

    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")

    testCompile("org.springframework.boot:spring-boot-starter-test")
    testCompile("com.jayway.restassured:rest-assured:2.8.0")
    testCompile("com.nhaarman:mockito-kotlin-kt1.1:1.5.0")
}
