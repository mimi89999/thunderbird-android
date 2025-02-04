import com.diffplug.gradle.spotless.SpotlessExtension

plugins {
    id("com.diffplug.spotless")
}

configure<SpotlessExtension> {
    kotlin {
        ktlint(libs.versions.ktlint.get())
            .setEditorConfigPath("$projectDir/.editorconfig")
            .editorConfigOverride(editorConfigOverride)
        target("**/*.kt")
        targetExclude("**/build/", "**/resources/", "plugins/openpgp-api-lib/")
    }
    kotlinGradle {
        ktlint(libs.versions.ktlint.get())
            .setEditorConfigPath("$projectDir/.editorconfig")
            .editorConfigOverride(editorConfigOverride)
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

val editorConfigOverride = mapOf(
    "ktlint_function_naming_ignore_when_annotated_with" to "Composable",
    "ktlint_standard_property-naming" to "disabled",
    "ktlint_standard_function-signature" to "disabled",
    "ktlint_standard_parameter-list-spacing" to "disabled",
    "ktlint_ignore_back_ticked_identifier" to "true",
)
