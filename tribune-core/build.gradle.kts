dependencies {
   api(libs.arrow.core)
   testImplementation(libs.kotest.assertions.core)
   testImplementation(libs.kotest.runner.junit5)
}

apply(from = "../publish-mpp.gradle.kts")
