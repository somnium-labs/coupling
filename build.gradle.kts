import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.ktlintidea)
    alias(libs.plugins.detekt)
    alias(libs.plugins.springframework.boot)
    alias(libs.plugins.spring.boot.dependency.management)
    alias(libs.plugins.kotlin.spring) // org.jetbrains.kotlin:kotlin-allopen
    alias(libs.plugins.kotlin.jpa) // org.jetbrains.kotlin:kotlin-noarg
}

subprojects {
    group = "com.roy.coupling"

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "16"
            javaParameters = true
            freeCompilerArgs = listOf("-Xjvm-default=all", "-Xopt-in=kotlin.RequiresOptIn")
        }
    }

    apply {
        plugin("kotlin")
        plugin("kotlin-kapt")
        plugin("io.spring.dependency-management")
        plugin("org.springframework.boot")
        plugin("kotlin-spring")
        plugin("kotlin-jpa")
    }

    allOpen {
        annotation("javax.persistence.Entity")
        annotation("javax.persistence.MappedSuperclass")
        annotation("javax.persistence.Embeddable")
    }

    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
        testImplementation("org.testcontainers:junit-jupiter:1.16.0")
    }
}

configure(subprojects.filter { it.name.contains("common") }) {}

tasks.detekt {
    dependsOn(tasks.ktlintFormat)
}

tasks.compileKotlin {
    dependsOn(tasks.detekt)
}

detekt {
    source = files("src/main/kotlin")
    config = files("$rootDir/detekt-config.yml")
    reports {
        xml {
            enabled = true
            destination = file("$buildDir/reports/detekt/detekt.xml")
        }
    }
}
