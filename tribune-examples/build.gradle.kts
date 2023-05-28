plugins {
   id("org.springframework.boot")
   kotlin("plugin.spring") version "1.7.10"
}

kotlin {

   sourceSets {

      val commonMain by getting {
         dependencies {
            api(projects.tribuneCore)
         }
      }

      val jvmMain by getting {
         dependencies {
            api(projects.tribuneKtor)
            api(projects.tribuneSpring)
            api(projects.tribuneExamplesModel)
            api(libs.ktor.server.netty)
            api(libs.ktor.server.content.negotiation)
            api(libs.ktor.serialization.jackson)
            api(libs.ktor.client.content.negotiation)
            api(libs.ktor.client.cio)
            api(libs.spring.boot.starter.web)
         }
      }

      all {
         languageSettings.optIn("kotlin.OverloadResolutionByLambdaReturnType")
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
