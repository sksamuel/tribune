plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
   id("org.springframework.boot") version "2.7.3"
   kotlin("plugin.spring") version "1.7.10"
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
               freeCompilerArgs += listOf("-Xcontext-receivers")
               jvmTarget = "11"
            }
         }
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            api(project(":tribune-core"))
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(project(":tribune-ktor"))
            implementation(Ktor.server.netty)
            implementation(Ktor.client.cio)
            api("io.ktor:ktor-serialization-jackson:_")
            api("io.ktor:ktor-server-content-negotiation:_")
            api("io.ktor:ktor-client-content-negotiation:_")
            implementation("org.springframework.boot:spring-boot-starter-web")
            implementation(project(":tribune-examples-model"))
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
