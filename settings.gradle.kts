rootProject.name = "adb-idea"

plugins {
    id("org.ajoberstar.reckon.settings") version "2.0.0"
}

reckon {
    setDefaultInferredScope("patch")
    scopeFromProp()
    snapshotFromProp()
}
