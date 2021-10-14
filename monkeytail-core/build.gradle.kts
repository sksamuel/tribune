plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
}

repositories {
   mavenCentral()
}

kotlin {

   targets {
      jvm()
      js {
         browser()
         nodejs()
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(Libs.Coroutines.core)
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
         dependencies {
            implementation(kotlin("reflect"))
            implementation("io.github.microutils:kotlin-logging-jvm:2.0.6")
         }
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
         dependencies {
            implementation(Libs.Kotest.shared)
            implementation(Libs.Kotest.assertions)
            implementation(Libs.Kotest.junit5)
         }
      }

      all {
         languageSettings.useExperimentalAnnotation("kotlin.time.ExperimentalTime")
         languageSettings.useExperimentalAnnotation("kotlin.experimental.ExperimentalTypeInference")
         languageSettings.useExperimentalAnnotation("kotlin.OverloadResolutionByLambdaReturnType")
      }
   }
}


tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions {
      jvmTarget = "1.8"
      apiVersion = "1.5"
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
