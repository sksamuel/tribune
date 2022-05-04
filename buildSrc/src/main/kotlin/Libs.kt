object Libs {

   const val kotlinVersion = "1.6.21"
   const val dokkaVersion = "0.10.1"

   object Kotest {
      private const val version = "5.3.0"
      const val shared = "io.kotest:kotest-assertions-shared:$version"
      const val assertions = "io.kotest:kotest-assertions-core:$version"
      const val junit5 = "io.kotest:kotest-runner-junit5:$version"
   }

   object Coroutines {
      private const val version = "1.6.1"
      const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$version"
   }
}
