dependencies {
   api(projects.tribuneCore)
   api(libs.arrow.core)
   implementation(libs.kotlinx.datetime)
   implementation(libs.kotest.assertions.core)
   implementation(libs.kotest.runner.junit5)
}

apply(from = "../publish-mpp.gradle.kts")
