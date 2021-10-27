dependencies {
    api(libs.spring.boot.starter.data.r2dbc)
    implementation(libs.r2dbc.mysql)
    implementation(libs.jackson.module.kotlin)

    api("io.github.microutils:kotlin-logging-jvm:2.0.11")
}
