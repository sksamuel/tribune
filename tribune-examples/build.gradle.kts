plugins {
   id("org.springframework.boot")
   kotlin("plugin.spring") version "1.7.21"
}

dependencies {
   api(projects.tribuneCore)
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
