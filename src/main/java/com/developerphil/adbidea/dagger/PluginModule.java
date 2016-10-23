package com.developerphil.adbidea.dagger;

import com.developerphil.adbidea.PluginPreferences;
import com.developerphil.adbidea.PluginPreferencesImpl;
import com.developerphil.adbidea.accessor.preference.PreferenceAccessor;
import com.developerphil.adbidea.accessor.preference.ProjectPreferenceAccessor;
import com.developerphil.adbidea.adb.Bridge;
import com.developerphil.adbidea.adb.BridgeImpl;
import com.developerphil.adbidea.adb.DeviceResultFetcher;
import com.developerphil.adbidea.adb.UseSameDevicesHelper;
import com.intellij.openapi.project.Project;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class PluginModule {

    private final Project project;

    public PluginModule(Project project) {
        this.project = project;
    }

    @Singleton
    @Provides Project provideProject() {
        return project;
    }

    @Singleton
    @Provides
    PreferenceAccessor providePreferenceAccessor() {
        return new ProjectPreferenceAccessor(project);
    }

    @Singleton
    @Provides
    PluginPreferences providePluginPreferences(PreferenceAccessor preferenceAccessor) {
        return new PluginPreferencesImpl(preferenceAccessor);
    }

    @Singleton
    @Provides
    UseSameDevicesHelper provideUseSameDeviceController(PluginPreferences preferences, Bridge bridge) {
        return new UseSameDevicesHelper(preferences, bridge);
    }

    @Provides
    DeviceResultFetcher provideDeviceResultFetcher(UseSameDevicesHelper useSameDevicesHelper, Bridge bridge) {
        return new DeviceResultFetcher(project, useSameDevicesHelper, bridge);
    }

    @Provides
    Bridge provideBridge(BridgeImpl bridgeImpl) {
        return bridgeImpl;
    }

}
