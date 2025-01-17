import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.atomic.fu)
    alias(libs.plugins.kotlinx.test.resources)
    id("kmp-conventions")
    id("linting-conventions")
    id("publishing-conventions")
}

kotlin {

    sourceSets {

        all {
            languageSettings {
                optIn("kotlin.ExperimentalUnsignedTypes")
            }
        }

       commonMain {
            dependencies {
                api(projects.decoder)
                api(libs.kotlinx.io.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
                implementation(libs.kotlinx.test.resources)
            }
        }
    }
}

configure<PublishingConventionsExtension> {
    name = "wasm-parser"
    description = "A wasm binary decoder for Kotlin Multiplatform"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = libs.versions.java.bytecode.version.get()
}
