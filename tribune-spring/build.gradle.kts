plugins {
   id("org.springframework.boot")
}

dependencies {
   api(projects.tribuneCore)
   api(libs.spring.boot.starter.web)
   testImplementation(projects.tribuneExamplesModel)
   testImplementation(libs.kotest.assertions.core)
   testImplementation(libs.kotest.runner.junit5)
}

apply(from = "../publish-mpp.gradle.kts")
