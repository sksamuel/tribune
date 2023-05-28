buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
   }
}

plugins {
   kotlin("multiplatform").version("1.7.21")
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

   apply(plugin = "org.jetbrains.kotlin.multiplatform")

   kotlin {
      targets {
         jvm()
      }
   }

   tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
      kotlinOptions {
         jvmTarget = "11"
         apiVersion = "1.6"
         languageVersion = "1.6"
      }
   }

   tasks.named<Test>("jvmTest") {
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
