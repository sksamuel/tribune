apply(plugin = "maven-publish")
apply(plugin = "signing")

repositories {
   mavenCentral()
}

val ossrhUsername: String by project
val ossrhPassword: String by project
val signingKey: String? by project
val signingPassword: String? by project

fun Project.publishing(action: PublishingExtension.() -> Unit) =
   configure(action)

fun Project.signing(configure: SigningExtension.() -> Unit): Unit =
   configure(configure)

val publications: PublicationContainer = (extensions.getByName("publishing") as PublishingExtension).publications

signing {
   useGpgCmd()
   if (signingKey != null && signingPassword != null) {
      @Suppress("UnstableApiUsage")
      useInMemoryPgpKeys(signingKey, signingPassword)
   }
   if (Ci.isRelease) {
      sign(publications)
   }
}

publishing {
   repositories {
      maven {
         val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
         val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots/")
         name = "deploy"
         url = if (Ci.isRelease) releasesRepoUrl else snapshotsRepoUrl
         credentials {
            username = java.lang.System.getenv("OSSRH_USERNAME") ?: ossrhUsername
            password = java.lang.System.getenv("OSSRH_PASSWORD") ?: ossrhPassword
         }
      }
   }

   publications.withType<MavenPublication>().forEach {
      it.apply {
         pom {
            name.set("tribune")
            description.set("Multiplatform Kotlin Validation")
            url.set("http://www.github.com/sksamuel/tribune")

            scm {
               connection.set("scm:git:http://www.github.com/sksamuel/tribune")
               developerConnection.set("scm:git:http://github.com/sksamuel")
               url.set("http://www.github.com/sksamuel/tribune")
            }

            licenses {
               license {
                  name.set("Apache-2.0")
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
