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
            api(libs.arrow.core)
         }
      }

      val jvmMain by getting {
         dependsOn(commonMain)
      }

      val jvmTest by getting {
         dependsOn(jvmMain)
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
