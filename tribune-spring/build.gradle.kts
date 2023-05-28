plugins {
   id("org.springframework.boot")
}

kotlin {

   targets {
      jvm()
   }

   sourceSets {

      val jvmMain by getting {
         dependencies {
            implementation(projects.tribuneCore)
            implementation(libs.spring.boot.starter.web)
            implementation(project(":tribune-examples-model"))
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
