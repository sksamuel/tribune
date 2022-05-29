buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
   }
}


plugins {
   kotlin("multiplatform")
   id("java-library")
   id("maven-publish")
   signing
}

allprojects {

   repositories {
      mavenCentral()
   }

   group = "com.sksamuel.optio"
   version = Ci.publishVersion

   tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
      kotlinOptions.jvmTarget = "11"
      kotlinOptions.apiVersion = "1.6"
      kotlinOptions.languageVersion = "1.6"
   }
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
      js(BOTH) {
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
      watchosX86()
      watchosX64()
      watchosSimulatorArm64()

      iosX64()
      iosArm64()
      iosArm32()
      iosSimulatorArm64()
   }
}

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (Ci.isRelease)
      sign(publications)
}
