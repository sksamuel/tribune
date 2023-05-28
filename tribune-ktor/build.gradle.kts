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
            implementation("io.ktor:ktor-server-content-negotiation:_")
            implementation("io.ktor:ktor-serialization-jackson:_")
            implementation(Testing.kotest.assertions.core)
            implementation(Testing.kotest.runner.junit5)
            implementation(Ktor.server.testHost)
         }
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
