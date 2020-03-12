package com.developerphil.adbidea.adb

import com.android.ddmlib.*
import com.android.ddmlib.log.LogReceiver
import com.android.sdklib.AndroidVersion
import java.io.File
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

data class FakeDevice(private val serialNumber: String) : IDevice {

    override fun getSerialNumber(): String {
        return serialNumber
    }

    // ---------------
    // NOT IMPLEMENTED
    // ---------------

    override fun installRemotePackage(p0: String?, p1: Boolean, p2: InstallReceiver?, vararg p3: String?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun installRemotePackage(p0: String?, p1: Boolean, p2: InstallReceiver?, p3: Long, p4: Long, p5: TimeUnit?, vararg p6: String?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun installPackage(p0: String?, p1: Boolean, p2: InstallReceiver?, vararg p3: String?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun installPackage(p0: String?, p1: Boolean, p2: InstallReceiver?, p3: Long, p4: Long, p5: TimeUnit?, vararg p6: String?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun executeShellCommand(p0: String?, p1: IShellOutputReceiver?, p2: Long, p3: Long, p4: TimeUnit?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun isOffline(): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun startScreenRecorder(p0: String?, p1: ScreenRecorderOptions?, p2: IShellOutputReceiver?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun reboot(p0: String?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getMountPoint(p0: String?): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getClients(): Array<out Client> {
        throw UnsupportedOperationException("not implemented")
    }

    override fun runLogService(p0: String?, p1: LogReceiver?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun installRemotePackage(p0: String?, p1: Boolean, vararg p2: String?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getVersion(): AndroidVersion {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getClientName(p0: Int): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun isOnline(): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun runEventLogService(p0: LogReceiver?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getLanguage(): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun root(): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun isBootLoader(): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getSystemProperty(p0: String?): Future<String> {
        throw UnsupportedOperationException("not implemented")
    }

    override fun isEmulator(): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getFileListingService(): FileListingService {
        throw UnsupportedOperationException("not implemented")
    }

    override fun isRoot(): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun removeForward(p0: Int, p1: Int) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun removeForward(p0: Int, p1: String?, p2: IDevice.DeviceUnixSocketNamespace?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun createForward(p0: Int, p1: Int) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun createForward(p0: Int, p1: String?, p2: IDevice.DeviceUnixSocketNamespace?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getAbis(): MutableList<String> {
        throw UnsupportedOperationException("not implemented")
    }

    override fun pushFile(p0: String?, p1: String?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun removeRemotePackage(p0: String?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getName(): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getClient(p0: String?): Client {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getBattery(): Future<Int> {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getBattery(p0: Long, p1: TimeUnit?): Future<Int> {
        throw UnsupportedOperationException("not implemented")
    }

    override fun executeShellCommand(p0: String?, p1: IShellOutputReceiver?, p2: Int) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun executeShellCommand(p0: String?, p1: IShellOutputReceiver?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun executeShellCommand(p0: String?, p1: IShellOutputReceiver?, p2: Long, p3: TimeUnit?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun hasClients(): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getPropertySync(p0: String?): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getProperties(): MutableMap<String, String> {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getProperty(p0: String?): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getAvdName(): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getRegion(): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getState(): IDevice.DeviceState {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getPropertyCacheOrSync(p0: String?): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun installPackages(p0: MutableList<File>?, p1: Boolean, p2: MutableList<String>?, p3: Long, p4: TimeUnit?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun pullFile(p0: String?, p1: String?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getDensity(): Int {
        throw UnsupportedOperationException("not implemented")
    }

    override fun uninstallPackage(p0: String?): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getSyncService(): SyncService {
        throw UnsupportedOperationException("not implemented")
    }

    override fun installPackage(p0: String?, p1: Boolean, vararg p2: String?) {
        throw UnsupportedOperationException("not implemented")
    }

    override fun syncPackageToDevice(p0: String?): String {
        throw UnsupportedOperationException("not implemented")
    }

    override fun arePropertiesSet(): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun supportsFeature(p0: IDevice.Feature?): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun supportsFeature(p0: IDevice.HardwareFeature?): Boolean {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getScreenshot(): RawImage {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getScreenshot(p0: Long, p1: TimeUnit?): RawImage {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getBatteryLevel(): Int {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getBatteryLevel(p0: Long): Int {
        throw UnsupportedOperationException("not implemented")
    }

    override fun getPropertyCount(): Int {
        throw UnsupportedOperationException("not implemented")
    }
}