kotlin {

   targets {
      jvm()
      js(IR) {
         browser()
         nodejs()
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            api(projects.tribuneCore)
            implementation(libs.kotlinx.datetime)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(libs.kotest.assertions.core)
            implementation(libs.kotest.runner.junit5)
         }
      }

      all {
         languageSettings.optIn("kotlin.OverloadResolutionByLambdaReturnType")
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
