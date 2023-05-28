plugins {
   kotlin("multiplatform")
   id("org.springframework.boot") version "2.7.3"
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
            implementation("org.springframework.boot:spring-boot-starter-web")
            implementation(project(":tribune-examples-model"))
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(Testing.kotest.assertions.core)
            implementation(Testing.kotest.runner.junit5)
         }
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
