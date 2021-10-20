dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// gradle version >= 7.2
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

rootProject.name = "coupling"
include("coupling-core")
include("coupling-common")
include("coupling-orchestration")
include("coupling-participant")

// example
include("coupling-example:order-service")
include("coupling-example:customer-service")
include("coupling-example:common")
