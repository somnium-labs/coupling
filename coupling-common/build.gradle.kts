dependencies {
    implementation(libs.spring.boot.data.r2dbc)
    implementation(libs.r2dbc.mysql)
    implementation(libs.jackson.module.kotlin)
    implementation(libs.spring.kafka)

    api("io.github.microutils:kotlin-logging-jvm:2.0.11")
}
