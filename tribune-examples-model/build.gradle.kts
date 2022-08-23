plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
   id("org.jetbrains.kotlinx.kover")
}

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

      val jvmMain by getting {
         dependencies {
            api(project(":tribune-core"))
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(Testing.kotest.assertions.core)
            implementation(Testing.kotest.runner.junit5)
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
   extensions.configure(kotlinx.kover.api.KoverTaskExtension::class) {
      isDisabled = false
      includes = listOf("com.sksamuel.*")
      excludes = listOf("com.sksamuel.examples.parser.*")
   }
}
