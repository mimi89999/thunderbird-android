import com.android.build.gradle.BasePlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinCompile

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.lint) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.spotless)
}

project.ext {
    testCoverage = project.hasProperty('testCoverage')
}

def javaVersion = JavaVersion.VERSION_11
def jvmTargetVersion = JvmTarget.JVM_11

allprojects {
    configurations.configureEach {
        resolutionStrategy.dependencySubstitution {
            substitute module("androidx.core:core") using module("androidx.core:core:${libs.versions.androidxCore.get()}")
            substitute module("androidx.activity:activity") using module("androidx.activity:activity:${libs.versions.androidxActivity.get()}")
            substitute module("androidx.activity:activity-ktx") using module("androidx.activity:activity-ktx:${libs.versions.androidxActivity.get()}")
            substitute module("androidx.fragment:fragment") using module("androidx.fragment:fragment:${libs.versions.androidxFragment.get()}")
            substitute module("androidx.fragment:fragment-ktx") using module("androidx.fragment:fragment-ktx:${libs.versions.androidxFragment.get()}")
            substitute module("androidx.appcompat:appcompat") using module("androidx.appcompat:appcompat:${libs.versions.androidxAppCompat.get()}")
            substitute module("androidx.preference:preference") using module("androidx.preference:preference:${libs.versions.androidxPreference.get()}")
            substitute module("androidx.recyclerview:recyclerview") using module("androidx.recyclerview:recyclerview:${libs.versions.androidxRecyclerView.get()}")
            substitute module("androidx.constraintlayout:constraintlayout") using module("androidx.constraintlayout:constraintlayout:${libs.versions.androidxConstraintLayout.get()}")
            substitute module("androidx.drawerlayout:drawerlayout") using module("androidx.drawerlayout:drawerlayout:${libs.versions.androidxDrawerLayout.get()}")
            substitute module("androidx.lifecycle:lifecycle-livedata") using module("androidx.lifecycle:lifecycle-livedata:${libs.versions.androidxLifecycle.get()}")
            substitute module("androidx.transition:transition") using module("androidx.transition:transition:${libs.versions.androidxTransition.get()}")
            substitute module("org.jetbrains:annotations") using module("org.jetbrains:annotations:${libs.versions.jetbrainsAnnotations.get()}")
            substitute module("org.jetbrains.kotlin:kotlin-stdlib") using module("org.jetbrains.kotlin:kotlin-stdlib:${libs.versions.kotlin.get()}")
            substitute module("org.jetbrains.kotlin:kotlin-stdlib-jdk7") using module("org.jetbrains.kotlin:kotlin-stdlib-jdk7:${libs.versions.kotlin.get()}")
            substitute module("org.jetbrains.kotlin:kotlin-stdlib-jdk8") using module("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${libs.versions.kotlin.get()}")
            substitute module("org.jetbrains.kotlinx:kotlinx-coroutines-android") using module("org.jetbrains.kotlinx:kotlinx-coroutines-android:${libs.versions.kotlinCoroutines.get()}")
        }
    }

    plugins.withType(BasePlugin).configureEach {
        project.android {
            compileSdk 33

            defaultConfig {
                minSdk 21
                targetSdk 31

                vectorDrawables.useSupportLibrary = true
                testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
            }

            compileOptions {
                sourceCompatibility javaVersion
                targetCompatibility javaVersion
            }

            lintOptions {
                abortOnError false
                lintConfig file("$rootProject.projectDir/config/lint/lint.xml")
            }

            testOptions {
                unitTests {
                    includeAndroidResources = true
                }
            }
        }
    }

    plugins.withType(JavaPlugin).configureEach {
        project.java {
            sourceCompatibility = javaVersion
            targetCompatibility = javaVersion
        }
    }

    tasks.withType(Test).configureEach {
        testLogging {
            exceptionFormat "full"
            showCauses true
            showExceptions true
            showStackTraces true
        }
    }

    tasks.withType(KotlinCompile).configureEach {
        compilerOptions {
            jvmTarget = jvmTargetVersion
        }
    }
}

spotless {
    kotlin {
        ktlint(libs.versions.ktlint.get())
        target("**/*.kt")
        targetExclude("**/build/", "**/resources/", "plugins/openpgp-api-lib/")
    }
    kotlinGradle {
        ktlint(libs.versions.ktlint.get())
        target("**/*.gradle.kts")
        targetExclude("**/build/")
    }
    format("markdown") {
        prettier()
        target("**/*.md")
        targetExclude("plugins/openpgp-api-lib/")
    }
    format("misc") {
        target("**/*.gradle", "**/.gitignore")
        trimTrailingWhitespace()
    }
}

tasks.register('testsOnCi') {
    dependsOn getSubprojects()
            .collect { project -> project.tasks.withType(Test) }
            .flatten()
            .findAll { task -> task.name in ['testDebugUnitTest', 'test'] }
}
