dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// gradle version >= 7.2
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "coupling"
include("coupling-test")
include("coupling-orchestration")
include("coupling-participant")
include("coupling-common")
