/*
 * This file was generated by the Gradle 'init' task.
 *
 * This generated file contains a sample Kotlin library project to get you started.
 * For more details take a look at the 'Building Java & JVM projects' chapter in the Gradle
 * User Manual available at https://docs.gradle.org/7.4.1/userguide/building_java_projects.html
 */

plugins {
    // Apply the org.jetbrains.kotlin.jvm Plugin to add support for Kotlin.
    id("org.jetbrains.kotlin.jvm") version "1.9.10"

    // https://plugins.grajlleitschuh.gradle.ktlintdle.org/plugin/org.
    id("org.jlleitschuh.gradle.ktlint") version "11.5.1"

    // https://plugins.gradle.org/plugin/org.sonarqube
    id("org.sonarqube") version "4.3.1.3277"

    // https://plugins.gradle.org/plugin/io.gitlab.arturbosch.detekt
    id("io.gitlab.arturbosch.detekt") version "1.23.1"

    // https://plugins.gradle.org/plugin/com.github.johnrengelman.shadow
    id("com.github.johnrengelman.shadow") version "8.1.1"

    // Apply the Jacoco plugin for test coverage.
    jacoco

    // Apply the java-library plugin for API and implementation separation.
    `java-library`

    // Apply the distribution plugin https://docs.gradle.org/current/userguide/distribution_plugin.html.
    distribution

    // Apply the maven-publish plugin to allow publishing to Maven.
    `maven-publish`
}

// Publishing properties defined at a project level gradle.properties.
val artifactId: String by project
val artifactGroup: String by project
val ver = "1.0.1"

ext {
    // Set the version.
    version = ver
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    // Use Maven Local for resolving dependencies.
    mavenLocal()
}

kotlin {
    // Use strict mode
    explicitApi = org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode.Strict
}

// Minimum jvmTarget of 1.8 needed since Kotlin 1.1
val targetLanguageLevel = "9"
tasks.compileKotlin {
    kotlinOptions.jvmTarget = targetLanguageLevel
    kotlinOptions.allWarningsAsErrors = true
}

tasks.compileTestKotlin {
    kotlinOptions.jvmTarget = targetLanguageLevel
}

tasks.compileJava {
    sourceCompatibility = targetLanguageLevel
    targetCompatibility = targetLanguageLevel
}

tasks.compileTestJava {
    sourceCompatibility = targetLanguageLevel
    targetCompatibility = targetLanguageLevel
}

dependencies {
    // Use the Kotlin Gradle plugin.
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")

    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // This dependency is used internally, and not exposed to consumers on their own compile classpath.
    implementation("com.google.guava:guava:30.1.1-jre")

    // https://mvnrepository.com/artifact/io.gitlab.arturbosch.detekt/detekt-gradle-plugin
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.1")

    // Use JUnit Jupiter API for testing.
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")

    // Use JUnit Jupiter Engine for testing.
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // This dependency is exported to consumers, that is to say found on their compile classpath.
    api("org.apache.commons:commons-math3:3.6.1")
    implementation(kotlin("reflect"))
}

// https://docs.gradle.org/current/userguide/publishing_gradle_module_metadata.html#sub:disabling-gmm-publication
tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

tasks.test {
    // Use junit platform for unit tests.
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        // Xml is required by SonarQube.
        xml.required.set(true)
        // HTML is easier to read.
        html.required.set(true)
    }
}

tasks.compileKotlin {
    // Treat all warnings as errors
    kotlinOptions.allWarningsAsErrors = true
}

// To perform SonarQube analysis these need to be set in the user level gradle.properties file.
val sonarToken: String by project
val sonarHost: String by project

sonarqube {
    properties {
        property("sonar.login", sonarToken)
        property("systemProp.sonar.host.url", sonarHost)
        property("sonar.java.coveragePlugin", "jacoco")
        property("systemProp.sonar.junit.reportsPath", "lib/build/test-results/test")
        property("systemProp.sonar.jacoco.reportPath", "lib/build/reports/jacoco/jacoco.xml")
    }
}

java {
    withSourcesJar()
}

ktlint {
    verbose.set(true)
    outputToConsole.set(true)
    coloredOutput.set(true)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.HTML)
    }
    filter {
        exclude("**/style-violations.kt")
    }
}

// Fixes a bug: https://github.com/JLLeitschuh/ktlint-gradle/issues/457
afterEvaluate {
    val f = tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.KtLintFormatTask>()
        .filter { it.name == "runKtlintFormatOverKotlinScripts" }
    tasks.withType<org.jlleitschuh.gradle.ktlint.tasks.KtLintFormatTask>()
        .filter { it.name != "runKtlintFormatOverKotlinScripts" }
        .forEach { it.dependsOn(f) }
}

// Publishing properties. These need to be either present in the user level gradle.properties file or
// specified as arguments when running the gradle publish task.
val publishUrl: String by project
val publishUsername: String by project
val publishPassword: String by project

publishing {
    repositories {
        maven {
            url = uri(publishUrl)
            credentials {
                username = publishUsername
                password = publishPassword
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = artifactGroup
            artifactId = artifactId

            from(components["java"])
        }
    }
}
