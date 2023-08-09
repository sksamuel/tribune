dependencies {
   api(projects.tribuneCore)
   api(libs.ktor.server.core)
   testImplementation(libs.ktor.server.content.negotiation)
   testImplementation(libs.ktor.serialization.jackson)
   testImplementation(libs.ktor.server.test.host)
   testImplementation(libs.kotest.assertions.core)
   testImplementation(libs.kotest.runner.junit5)
}

apply(from = "../publish-mpp.gradle.kts")
