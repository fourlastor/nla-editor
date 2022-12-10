import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.utils.addToStdlib.ifTrue

@Suppress(
    // known false positive: https://youtrack.jetbrains.com/issue/KTIJ-19369
    "DSL_SCOPE_VIOLATION"
)
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose)
}

group = "io.github.fourlastor"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
            (extra["io.github.fourlastor.nla.compose.reports"].toString().toBoolean()).ifTrue {
                kotlinOptions.freeCompilerArgs += listOf(
                    "-P",
                    "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.rootDir.absolutePath}/reports"
                )
            }
        }
        withJava()
    }
    @Suppress("UNUSED_VARIABLE")
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs) {
                    exclude("org.jetbrains.compose.material")
                }
                implementation(libs.decompose)
                implementation(libs.decomposeCompose)
                implementation(libs.expui)
                implementation(libs.immutableCollections)
                implementation(libs.lwjgl)
                implementation(libs.lwjglNfd)
                implementation(libs.okio)
                implementation(libs.serializationJson)
                val natives = arrayOf("linux", "macos", "macos-arm64", "windows")
                for (distribution in natives) {
                    runtimeOnly("${libs.lwjgl.get()}:natives-$distribution")
                    runtimeOnly("${libs.lwjglNfd.get()}:natives-$distribution")
                }
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            modules("jdk.unsupported")
            packageName = "nla-editor"
            packageVersion = "1.0.0"
        }
    }
}
