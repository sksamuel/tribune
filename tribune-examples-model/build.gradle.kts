plugins {
   id("java")
   kotlin("multiplatform")
   id("java-library")
}

group = "com.sksamuel.tribune"
version = "1.3.0-LOCAL"

repositories {
   mavenCentral()
}

kotlin {

   targets {
      jvm {
         compilations.all {
            kotlinOptions {
               jvmTarget = "11"
            }
         }
      }
   }

   sourceSets {

      val commonMain by getting {
         dependencies {
            implementation(project(":tribune-core"))
         }
      }

      val jvmMain by getting {
         dependencies {
            implementation(project(":tribune-core"))
         }
      }

      all {
         languageSettings.optIn("kotlin.OverloadResolutionByLambdaReturnType")
      }
   }
}

dependencies {
   testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
   testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
   useJUnitPlatform()
}
