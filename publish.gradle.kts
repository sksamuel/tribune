val ossrhUsername: String by project
val ossrhPassword: String by project

fun Project.publishing(action: PublishingExtension.() -> Unit) =
   configure(action)

fun Project.signing(configure: SigningExtension.() -> Unit): Unit =
   configure(configure)

fun Project.java(configure: JavaPluginExtension.() -> Unit): Unit =
   configure(configure)


val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (Ci.isRelease)
      sign(publications)
}

java {
   targetCompatibility = org.gradle.api.JavaVersion.VERSION_1_8
   withJavadocJar()
   withSourcesJar()
}

publishing {
   repositories {
      maven {
         name = "deploy"
         val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
         val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
         url = if (Ci.isRelease) releasesRepoUrl else snapshotsRepoUrl
         credentials {
            username = System.getenv("OSSRH_USERNAME") ?: ossrhUsername
            password = System.getenv("OSSRH_PASSWORD") ?: ossrhPassword
         }
      }
   }

   publications {
      register("mavenJava", MavenPublication::class) {
         from(components["java"])
         pom {
            name.set("princeps")
            description.set("Multiplatform Kotlin Validation")
            url.set("http://www.github.com/sksamuel/princeps")

            scm {
               connection.set("scm:git:http://www.github.com/sksamuel/princeps")
               developerConnection.set("scm:git:http://github.com/sksamuel")
               url.set("http://www.github.com/sksamuel/princeps")
            }

            licenses {
               license {
                  name.set("The Apache 2.0 License")
                  url.set("https://opensource.org/licenses/Apache-2.0")
               }
            }

            developers {
               developer {
                  id.set("sksamuel")
                  name.set("Stephen Samuel")
                  email.set("sam@sksamuel.com")
               }
            }
         }

      }
   }
}

