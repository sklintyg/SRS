import org.gradle.internal.os.OperatingSystem
import se.inera.intyg.srs.build.Config.Dependencies
import se.inera.intyg.srs.build.Config.TestDependencies

val localBuild = project.gradle.startParameter.taskNames.contains("bootRun")
var isIt:Boolean? = false;
ext {
    isIt = System.getProperty("env")?.equals("it")
}
var port:String? = System.getProperty("port")?:"8080"

plugins {
    war
    kotlin("plugin.jpa")
    id("org.springframework.boot")
}

dependencies {
    // Project dependencies
    implementation("se.inera.intyg.clinicalprocess.healthcond.srs:intyg-clinicalprocess-healthcond-srs-schemas:${Dependencies.srsSchemasVersion}")
    implementation("se.riv.itintegration.monitoring:itintegration-monitoring-schemas:${Dependencies.monitoringSchemasVersion}")

    // External dependencies
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.apache.cxf:cxf-spring-boot-starter-jaxws:${Dependencies.cxfBootStarterVersion}")
    implementation("org.liquibase:liquibase-core:${Dependencies.liquibaseVersion}")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.9")
    implementation("org.nuiton.thirdparty:JRI:0.9-9")
    implementation("org.apache.poi:poi-ooxml:4.0.1")

    runtime("com.h2database:h2")
    runtime("mysql:mysql-connector-java")

    // Test dependencies
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.1.0")
    testImplementation("org.exparity:hamcrest-date:2.0.5")
    testImplementation("io.rest-assured:rest-assured:${TestDependencies.restAssuredVersion}")
    testImplementation("io.rest-assured:json-schema-validator:${TestDependencies.restAssuredVersion}")
    testImplementation("io.rest-assured:json-schema-validator:${TestDependencies.restAssuredVersion}")
    testImplementation("io.rest-assured:json-path:${TestDependencies.restAssuredVersion}")
    testImplementation("io.rest-assured:xml-path:${TestDependencies.restAssuredVersion}")
}

springBoot {
    buildInfo()
}

tasks {

    val pathingJar by creating(Jar::class) {
        dependsOn(configurations.runtime)
        archiveAppendix.set("pathing")

        doFirst {
            manifest {
                val classpath = configurations.runtimeClasspath.get().files
                        .map { it.toURI().toURL().toString().replaceFirst("file:/", "/") }
                        .joinToString(separator = " ")

                val mainClass = "se.inera.intyg.srs.Application"

                attributes["Class-Path"] = classpath
                attributes["Main-Class"] = mainClass
            }
        }
    }

    val restAssuredTest by creating(Test::class) {
        outputs.upToDateWhen { false }
        systemProperty("integration.tests.baseUrl", System.getProperty("baseUrl", "http://localhost:8080"))
        include("**/*IT*")
    }

    test {
        exclude("**/*IT*")
    }

    bootJar {
        manifest {
            attributes("Main-Class" to "org.springframework.boot.loader.PropertiesLauncher")
            attributes("Start-Class" to  "se.inera.intyg.srs.Application")
        }
    }

    if (OperatingSystem.current().isWindows) {
        println("Operating system is windows")
        bootRun {
            dependsOn(pathingJar)

            doFirst {
                classpath = files(
                        "${project.projectDir}/build/classes/java/main",
                        "${project.projectDir}/build/classes/kotlin/main",
                        "${project.projectDir}/src/main/resources",
                        "${project.projectDir}/build/resources/main",
                        pathingJar.archiveFile)
            }
        }
    }

    bootRun {
        if (isIt == true) {
            jvmArgs = listOf(
                    "-Dspring.profiles.active=runtime, it",
                    "-Djava.library.path=/usr/local/lib/R/3.5/site-library/rJava/jri",
                    "-Dloader.path=WEB-INF/lib-provided,WEB-INF/lib,WEB-INF/classes",
                    "-Dserver.port=${port}")
        } else {
            jvmArgs = listOf(
                    "-Dspring.profiles.active=runtime",
                    "-Djava.library.path=/usr/local/lib/R/3.5/site-library/rJava/jri",
                    "-Dloader.path=WEB-INF/lib-provided,WEB-INF/lib,WEB-INF/classes",
                    "-Dserver.port=${port}")
        }
    }
}