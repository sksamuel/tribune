buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
      gradlePluginPortal()
   }
}


plugins {
   java
   kotlin("multiplatform").version(Libs.kotlinVersion)
   id("java-library")
   id("maven-publish")
   signing
   id("org.jetbrains.dokka").version(Libs.dokkaVersion)
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
   }
}

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (Ci.isRelease)
      sign(publications)
}
