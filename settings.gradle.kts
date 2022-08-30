plugins {
   // See https://jmfayard.github.io/refreshVersions
   id("de.fayard.refreshVersions") version "0.40.2"
}

refreshVersions {
   enableBuildSrcLibs()
   rejectVersionIf {
      candidate.stabilityLevel != de.fayard.refreshVersions.core.StabilityLevel.Stable
   }
}

include("tribune-core")
include("tribune-datetime")
include("tribune-examples-model")
include("tribune-examples")
include("tribune-ktor")
include("tribune-spring")
