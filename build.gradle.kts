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
               jvmTarget = "1.8"
            }
         }
      }
   }
}
