kotlin {

   targets {
      jvm()
   }

   sourceSets {

      val jvmMain by getting {
         dependencies {
            api(project(":tribune-core"))
            api(Ktor.server.core)
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
