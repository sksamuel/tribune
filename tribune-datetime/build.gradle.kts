kotlin {

   targets {
      jvm()
      js(IR) {
         browser()
         nodejs()
      }

      linuxX64()

      mingwX64()

      macosX64()
      macosArm64()

      tvos()
      tvosSimulatorArm64()

      watchosArm32()
      watchosArm64()
      watchosX64()
      watchosSimulatorArm64()

      iosX64()
      iosArm64()
      iosSimulatorArm64()
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            api(projects.tribuneCore)
            implementation(KotlinX.datetime)
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation(Testing.kotest.assertions.core)
            implementation(Testing.kotest.runner.junit5)
         }
      }

      all {
         languageSettings.optIn("kotlin.OverloadResolutionByLambdaReturnType")
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
