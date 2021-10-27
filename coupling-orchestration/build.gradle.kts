dependencies {
    implementation(projects.coupling.couplingCommon)
    implementation(projects.coupling.couplingMessaging)
    api(projects.coupling.couplingConsumer)
    api(projects.coupling.couplingProducer)

    implementation(libs.arrow.core)
    implementation(libs.arrow.fx.coroutines)
}
