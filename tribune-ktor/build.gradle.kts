kotlin {

   targets {
      jvm()
   }

   sourceSets {

      val jvmMain by getting {
         dependencies {
            api(project(":tribune-core"))
            api("io.ktor:ktor-server-core:2.0.3")
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation("io.ktor:ktor-server-content-negotiation:2.0.3")
            implementation("io.ktor:ktor-serialization-jackson:2.0.3")
            implementation(Testing.kotest.assertions.core)
            implementation(Testing.kotest.runner.junit5)
            implementation("io.ktor:ktor-server-test-host:2.0.3")
         }
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
