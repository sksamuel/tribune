kotlin {

   sourceSets {

      val jvmMain by getting {
         dependencies {
            api(projects.tribuneCore)
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
