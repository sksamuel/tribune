plugins {
   kotlin("multiplatform")
   id("org.springframework.boot")
}

apply(plugin = "io.spring.dependency-management")

repositories {
   mavenCentral()
}

kotlin {

   targets {
      jvm()
   }

   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(projects.tribuneCore)
            implementation(projects.tribuneExamplesModel)
            implementation(libs.spring.boot.starter.web)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.runner.junit5)
         }
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
