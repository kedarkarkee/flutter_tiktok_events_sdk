import com.android.build.gradle.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val isStandaloneAndroidModuleBuild = project == rootProject

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        if (project == rootProject) {
            classpath("com.android.tools.build:gradle:8.13.0")
            classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.20")
            classpath("com.github.ben-manes:gradle-versions-plugin:0.53.0")
        }
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

apply(plugin = "com.android.library")
if (isStandaloneAndroidModuleBuild) {
    apply(plugin = "com.github.ben-manes.versions")
}

val agpVersion: String = com.android.Version.ANDROID_GRADLE_PLUGIN_VERSION
val agpMajorVersion = agpVersion.substringBefore(".").toIntOrNull() ?: 0
val builtInKotlin = findProperty("android.builtInKotlin")
    ?.toString()
    ?.toBooleanStrictOrNull()
    ?: (agpMajorVersion >= 9)
if (!builtInKotlin) {
    apply(plugin = "kotlin-android")
}

configure<LibraryExtension> {
    namespace = "com.example.tiktok_events_sdk"
    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin")
        }
        getByName("test") {
            java.srcDirs("src/test/kotlin")
        }
    }

    defaultConfig {
        minSdk = 24
        consumerProguardFiles("consumer-rules.pro")
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()

            it.outputs.upToDateWhen { false }
            it.testLogging {
                events("passed", "skipped", "failed", "standardOut", "standardError")
                showStandardStreams = true
            }
        }
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    "implementation"("com.github.tiktok:tiktok-business-android-sdk:1.6.1")
    "implementation"("androidx.lifecycle:lifecycle-process:2.10.0")
    "testImplementation"("org.jetbrains.kotlin:kotlin-test")
    "testImplementation"("org.mockito:mockito-core:5.23.0")
}
