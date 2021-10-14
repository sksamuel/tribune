buildscript {
   repositories {
      mavenCentral()
      mavenLocal()
      gradlePluginPortal()
   }
}

plugins {
   java
   kotlin("multiplatform") version Libs.kotlinVersion
   id("java-library")
   id("maven-publish")
   signing
   id("org.jetbrains.dokka") version Libs.dokkaVersion
}

tasks {
   javadoc {
   }
}

allprojects {

   repositories {
      mavenCentral()
      google()
      maven("https://kotlin.bintray.com/kotlinx")
      maven("https://dl.bintray.com/kotlin/kotlin-eap")
   }

   group = "com.sksamuel.monkeytail"
   version = Ci.publishVersion
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

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
   kotlinOptions.jvmTarget = "1.8"
   kotlinOptions.apiVersion = "1.5"
}

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (Ci.isRelease)
      sign(publications)
}
