buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
   }
}

plugins {
   kotlin("jvm").version("1.9.21")
   id("java-library")
   id("maven-publish")
   signing
}

allprojects {

   repositories {
      mavenCentral()
   }

   group = "com.sksamuel.tribune"
   version = Ci.publishVersion

   apply(plugin = "org.jetbrains.kotlin.jvm")

   java {
      sourceCompatibility = JavaVersion.VERSION_11
   }
   tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
      kotlinOptions {
         jvmTarget = "11"
         apiVersion = "1.9"
         languageVersion = "1.9"
      }
   }

   tasks.withType<Test> {
      useJUnitPlatform()
      filter {
         isFailOnNoMatchingTests = false
      }
      testLogging {
         showExceptions = true
         showStandardStreams = true
         events = setOf(
            org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED,
            org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
         )
         exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
      }
   }
}
