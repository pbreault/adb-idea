import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.Changelog.OutputType.HTML
import org.jetbrains.changelog.Changelog.OutputType.MARKDOWN

plugins {
    // Must match the Kotlin version bundled with the IDE
    // https://plugins.jetbrains.com/docs/intellij/using-kotlin.html#kotlin-standard-library
    // https://plugins.jetbrains.com/docs/intellij/android-studio-releases-list.html
    id("org.jetbrains.kotlin.jvm") version "1.8.0"

    // https://github.com/JetBrains/gradle-intellij-plugin
    id("org.jetbrains.intellij") version "1.17.4"

    // https://github.com/ajoberstar/reckon
    id("org.ajoberstar.reckon") version "0.14.0"

    // https://github.com/b3er/gradle-local-properties-plugin
    id("com.github.b3er.local.properties") version "1.1"

    // https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "2.0.0"

}

repositories {
    mavenCentral()
}

group = "com.developerphil.intellij.plugin.adbidea"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

intellij {
    pluginName.set("adb_idea")
    updateSinceUntilBuild.set(false)
    type.set("AI")
    plugins.set(listOf("android"))

    // Set this path to point to a locally installed version of android studio.
    // see gradle.properties for more info
    if (project.hasProperty("localIdeOverride")) {
        localPath.set(property("localIdeOverride").toString())
    } else {
        version.set(property("ideVersion").toString())
    }

    tasks.instrumentCode {
        compilerVersion.set("231.9225.16")
    }


    tasks.patchPluginXml {
        sinceBuild.set(project.property("sinceBuild").toString())

        // Extract the most recent entries in CHANGELOG.md and add them to the change notes.
        changeNotes.set(provider { recentChanges(HTML) })
    }

    tasks.runIde {
        jvmArgs = listOf("-Xmx4096m", "-XX:+UnlockDiagnosticVMOptions")
    }
}

changelog {
    repositoryUrl.set("https://github.com/pbreault/adb-idea")
    itemPrefix.set("-")
    keepUnreleasedSection.set(true)
    unreleasedTerm.set("[Unreleased]")
    groups.empty()
    combinePreReleases.set(true)
}

tasks.register("printLastChanges") {
    doLast {
        println(recentChanges(outputType = MARKDOWN))
    }
}

fun recentChanges(outputType: Changelog.OutputType): String {
    var s = ""
    changelog.getAll()
        .toList()
        .drop(1) // drop the [Unreleased] section
        .take(5) // last 5 changes
        .forEach { (key, _) ->
            s += changelog.renderItem(
                changelog
                    .get(key)
                    .withHeader(false)
                    .withEmptySections(false),
                outputType
            )
        }

    return s
}

dependencies {
    implementation("org.jooq:joor-java-8:0.9.14")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.7.0")
    testImplementation("com.google.truth:truth:1.1.3")
}

reckon {
    scopeFromProp()
    snapshotFromProp()
}

tasks.buildSearchableOptions {
    isEnabled = false
}