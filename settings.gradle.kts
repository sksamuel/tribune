rootProject.name = "tribune"

include(
   ":tribune-core",
   ":tribune-datetime",
   ":tribune-examples-model",
   ":tribune-examples",
   ":tribune-ktor",
   ":tribune-spring",
)

pluginManagement {
   plugins {
      id("org.springframework.boot") version ("2.7.12")
   }
}

enableFeaturePreview("STABLE_CONFIGURATION_CACHE")
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

dependencyResolutionManagement {
   versionCatalogs {
      create("libs") {

         library("kotlinx-datetime", "org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")

         val ktor = "2.3.3"
         library("ktor-server-core", "io.ktor:ktor-server-core:$ktor")
         library("ktor-server-netty", "io.ktor:ktor-server-netty:$ktor")
         library("ktor-server-test-host", "io.ktor:ktor-server-test-host:$ktor")
         library("ktor-server-content-negotiation", "io.ktor:ktor-server-content-negotiation:$ktor")
         library("ktor-client-cio", "io.ktor:ktor-client-cio:$ktor")
         library("ktor-client-content-negotiation", "io.ktor:ktor-client-content-negotiation:$ktor")
         library("ktor-serialization-jackson", "io.ktor:ktor-serialization-jackson:$ktor")

         library("arrow-core", "io.arrow-kt:arrow-core:2.0.0")
         library("spring-boot-starter-web", "org.springframework.boot:spring-boot-starter-web:2.7.12")

         val kotest = "5.8.0"
         library("kotest-assertions-core", "io.kotest:kotest-assertions-core:$kotest")
         library("kotest-runner-junit5", "io.kotest:kotest-runner-junit5:$kotest")
      }
   }
}
