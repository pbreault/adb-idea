package com.developerphil.adbidea.compatibility

import com.android.ddmlib.IDevice
import com.android.tools.idea.run.*
import org.jetbrains.android.facet.AndroidFacet
import org.joor.on
import org.joor.asType

class CanRunOnDeviceCompat(private val myFacet: AndroidFacet, device: IDevice) : BackwardCompatibleGetter<LaunchCompatibility>() {

    private val androidDevice = ConnectedAndroidDevice(device, null)

    override// Android Studio 3.4+
    fun getCurrentImplementation() = on<LaunchCompatibilityCheckerImpl>()
            .call("create", myFacet, null, null)
            .asType<LaunchCompatibilityChecker>()
            .validate(androidDevice)

    override// Android Studio 3.3 & Intellij 2018.3
    fun getPreviousImplementation() = on<LaunchCompatibilityCheckerImpl>()
            .call("create", myFacet)
            .asType<LaunchCompatibilityChecker>()
            .validate(androidDevice)
}
