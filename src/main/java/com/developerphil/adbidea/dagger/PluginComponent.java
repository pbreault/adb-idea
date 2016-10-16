package com.developerphil.adbidea.dagger;

import com.developerphil.adbidea.adb.DeviceResultFetcher;
import com.developerphil.adbidea.ui.DeviceChooserDialog;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = PluginModule.class)
public interface PluginComponent {
    void inject(DeviceChooserDialog deviceChooserDialog);

    DeviceResultFetcher getDeviceResultFetcher();
}
