package com.developerphil.adbidea.dagger;

import com.developerphil.adbidea.adb.DeviceResultFetcher;
import com.developerphil.adbidea.PluginPreferences;
import com.developerphil.adbidea.PluginPreferencesImpl;
import com.developerphil.adbidea.accessor.preference.PreferenceAccessor;
import com.developerphil.adbidea.accessor.preference.ProjectPreferenceAccessor;
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
    @Provides
    PreferenceAccessor providePreferenceAccessor() {
        return new ProjectPreferenceAccessor(project);
    }

    @Singleton
    @Provides
    PluginPreferences providePluginPreferences(PreferenceAccessor preferenceAccessor) {
        return new PluginPreferencesImpl(preferenceAccessor);
    }

    @Provides
    DeviceResultFetcher provideDeviceResultFetcher() {
        return new DeviceResultFetcher(project);
    }

}
