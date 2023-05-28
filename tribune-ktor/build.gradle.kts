kotlin {

   targets {
      jvm()
   }

   sourceSets {

      val jvmMain by getting {
         dependencies {
            api(project(":tribune-core"))
            api(libs.ktor.server.core)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(libs.ktor.server.content.negotiation)
            implementation(libs.ktor.serialization.jackson)
            implementation(libs.ktor.server.test.host)
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.runner.junit5)
         }
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
