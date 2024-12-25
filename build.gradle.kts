import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.Changelog.OutputType.HTML
import org.jetbrains.changelog.Changelog.OutputType.MARKDOWN

plugins {
    // Must match the Kotlin version bundled with the IDE
    // https://plugins.jetbrains.com/docs/intellij/using-kotlin.html#kotlin-standard-library
    // https://plugins.jetbrains.com/docs/intellij/android-studio-releases-list.html
    id("org.jetbrains.kotlin.jvm") version "2.1.0"

    // https://github.com/JetBrains/intellij-platform-gradle-plugin
    id("org.jetbrains.intellij.platform") version "2.1.0"

    // https://github.com/ajoberstar/reckon
    id("org.ajoberstar.reckon") version "0.14.0"

    // https://github.com/b3er/gradle-local-properties-plugin
    id("com.github.b3er.local.properties") version "1.1"

    // https://github.com/JetBrains/gradle-changelog-plugin
    id("org.jetbrains.changelog") version "2.2.1"

}
// Readying the code for org.jetbrains.intellij.platform : 2.2.1
//project.configurations.all {
//    exclude(Constants.Configurations.Dependencies.BUNDLED_MODULE_GROUP, "com.jetbrains.performancePlugin")
//}
repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

intellijPlatform {
    pluginConfiguration {
        name = "adb_idea"
        group = "com.developerphil.intellij.plugin.adbidea"
        changeNotes.set(provider { recentChanges(HTML) })
        ideaVersion.sinceBuild.set(project.property("sinceBuild").toString())
        ideaVersion.untilBuild.set(provider { null })
    }
    buildSearchableOptions.set(false)
    instrumentCode = true
}

changelog {
    repositoryUrl.set("https://github.com/pbreault/adb-idea")
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

kotlin {
    jvmToolchain(21)
}

tasks.runIde {
    jvmArgs = listOf("-Xmx4096m", "-XX:+UnlockDiagnosticVMOptions")
}

tasks.register("printLastChanges") {
    doLast {
        println(recentChanges(outputType = MARKDOWN))
        println(recentChanges(outputType = HTML))
    }
}
val localIdePath: String? by project.extra
localIdePath?.let {
    val runLocalIde by intellijPlatformTesting.runIde.registering {
        localPath.set(file(it))
    }
}

dependencies {
    intellijPlatform {
        bundledPlugin("org.jetbrains.android")
        instrumentationTools()
// Readying the code for org.jetbrains.intellij.platform : 2.2.1
//        plugin("org.jetbrains.android", property("androidPluginVersion").toString())
//        plugin("com.android.tools.design", property("androidPluginVersion").toString())
        if (project.hasProperty("localIdeOverride")) {
            local(property("localIdeOverride").toString())
        } else {
            androidStudio(property("ideVersion").toString())
        }

    }

    implementation("org.jooq:joor:0.9.15")

    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:4.7.0")
    testImplementation("com.google.truth:truth:1.4.4")
}

fun recentChanges(outputType: Changelog.OutputType): String {
    var s = ""
    changelog.getAll().toList().drop(1) // drop the [Unreleased] section
        .take(5) // last 5 changes
        .forEach { (key, _) ->
            s += changelog.renderItem(
                changelog.get(key).withHeader(true).withEmptySections(false), outputType
            )
        }

    return s
}

