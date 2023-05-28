plugins {
   id("java")
   id("java-library")
   id("org.springframework.boot") version "2.7.3"
   kotlin("plugin.spring") version "1.7.10"
}

apply(plugin = "io.spring.dependency-management")

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
            implementation(Ktor.server.netty)
            implementation(Ktor.client.cio)
            api("io.ktor:ktor-serialization-jackson:_")
            api("io.ktor:ktor-server-content-negotiation:_")
            api("io.ktor:ktor-client-content-negotiation:_")

            implementation(project(":tribune-spring"))
            implementation("org.springframework.boot:spring-boot-starter-web")
            implementation(project(":tribune-examples-model"))
         }
      }

      all {
         languageSettings.optIn("kotlin.OverloadResolutionByLambdaReturnType")
      }
   }
}

apply(from = "../publish-mpp.gradle.kts")
