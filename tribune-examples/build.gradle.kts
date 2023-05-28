plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
   id("org.springframework.boot")
   kotlin("plugin.spring") version "1.8.21"
}

apply(plugin = "io.spring.dependency-management")

repositories {
   mavenCentral()
}

kotlin {

   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "11"
            }
         }
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            api(projects.tribuneCore)
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(projects.tribuneKtor)
            implementation(projects.tribuneSpring)
            implementation(projects.tribuneExamplesModel)

            api(libs.ktor.server.core)
            api(libs.ktor.server.netty)
            api(libs.ktor.client.cio)
            api(libs.ktor.serialization.jackson)
            api(libs.ktor.server.content.negotiation)
            api(libs.ktor.client.content.negotiation)

            implementation(libs.spring.boot.starter.web)
         }
      }

      all {
         languageSettings.optIn("kotlin.OverloadResolutionByLambdaReturnType")
      }
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

apply(from = "../publish-mpp.gradle.kts")
