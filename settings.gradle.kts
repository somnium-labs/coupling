rootProject.name = "coupling"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

// gradle version >= 7.2
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("VERSION_CATALOGS")

include("coupling-common")
include("coupling-orchestration")
include("coupling-participant")
include("coupling-messaging")
include("coupling-consumer")
include("coupling-producer")

// example
include("coupling-example:order-service")
include("coupling-example:customer-service")
include("coupling-example:common")
