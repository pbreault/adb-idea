import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.Changelog.OutputType.HTML
import org.jetbrains.changelog.Changelog.OutputType.MARKDOWN

plugins {
    // Must match the Kotlin version bundled with the IDE
    // https://plugins.jetbrains.com/docs/intellij/using-kotlin.html#kotlin-standard-library
    // https://plugins.jetbrains.com/docs/intellij/android-studio-releases-list.html
    id("org.jetbrains.kotlin.jvm") version "2.1.20"

    // https://github.com/JetBrains/intellij-platform-gradle-plugin
    id("org.jetbrains.intellij.platform") version "2.6.0"

    // https://github.com/ajoberstar/reckon
    id("org.ajoberstar.reckon") version "0.14.0"

    // https://github.com/b3er/gradle-local-properties-plugin
    id("com.github.b3er.local.properties") version "1.1"

    // https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "2.4.0"
}

repositories {
    mavenCentral()
    intellijPlatform { defaultRepositories() }
}

tasks.withType<org.jetbrains.intellij.platform.gradle.tasks.BuildPluginTask> {
    archiveBaseName.set("adb-menu")
}

intellijPlatform {
    pluginConfiguration {
        name = "ADB Menu"
        group = "io.github.raghavsatyadev"
        changeNotes.set(provider { recentChanges(HTML) })
        ideaVersion.sinceBuild.set(project.property("canaryIntelliJMinSupport").toString())
        ideaVersion.untilBuild.set(provider { null })
    }
    buildSearchableOptions.set(false)
    instrumentCode = true
}

changelog {
    repositoryUrl.set("https://github.com/raghavsatyadev/adb-menu")
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.empty()
    combinePreReleases.set(true)
}

reckon {
    scopeFromProp()
    snapshotFromProp()
}

kotlin { jvmToolchain(21) }

tasks.runIde { jvmArgs = listOf("-Xmx4096m", "-XX:+UnlockDiagnosticVMOptions") }

tasks.register<DefaultTask>("printLastChanges") {
    notCompatibleWithConfigurationCache("Uses recentChanges function which is not cacheable")
    doLast {
        println(recentChanges(outputType = MARKDOWN))
        println(recentChanges(outputType = HTML))
    }
}

// Read `localIdePath` safely via providers so missing property doesn't fail the build
val localIdePath: String? = providers.gradleProperty("localIdePath").orNull

// Removed previous registration via `intellijPlatformTesting.runIde.registering` because
// it forced evaluation of runtimeDirectory / dependency resolution at configuration time.
// Provide a simple alias task to keep IDE run configurations that point to `runLocalIde` working.
tasks.register<DefaultTask>("runLocalIde") {
    group = "Intellij platform"
    description = "Run the IDE using the local installation if available (alias to runIde)."
    dependsOn(tasks.named("runIde"))
}

dependencies {
    intellijPlatform {
        val version: String
        val pluginVersion: String
        val idePath = property("idePath")
        if (idePath == "Canary") {
            version = property("canaryIdeVersion").toString()
            pluginVersion = property("canaryIntelliJMinSupport").toString()
        } else {
            version = property("stableIdeVersion").toString()
            pluginVersion = property("stableIntelliJMinSupport").toString()
        }

        val ide = property("ideOverride").toString()

        when {
            // Use local IDE installation if the path property is provided
            localIdePath != null && localIdePath.isNotBlank() -> {
                if (ide == "IC" || ide == "IU") {
                    plugin("org.jetbrains.android:$pluginVersion")
                } else {
                    bundledPlugin("org.jetbrains.android")
                }
                // Pass a File so the dependency helper recognizes it as a local IDE
                local(file(localIdePath))
            }

            ide == "IC" -> {
                plugin("org.jetbrains.android:$pluginVersion")
                intellijIdeaCommunity(version)
            }

            ide == "IU" -> {
                plugin("org.jetbrains.android:$pluginVersion")
                intellijIdeaUltimate(version)
            }

            else -> {
                bundledPlugin("org.jetbrains.android")
                androidStudio(version)
            }
        }
    }

    implementation("org.jooq:joor:0.9.15")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.7.0")
    testImplementation("com.google.truth:truth:1.4.4")
}

fun recentChanges(outputType: Changelog.OutputType): String {
    var s = ""
    changelog
        .getAll()
        .toList()
        .drop(1) // drop the [Unreleased] section
        .take(5) // last 5 changes
        .forEach { (key, _) ->
            s +=
                changelog.renderItem(
                    changelog.get(key).withHeader(true).withEmptySections(false),
                    outputType,
                )
        }

    return s
}
