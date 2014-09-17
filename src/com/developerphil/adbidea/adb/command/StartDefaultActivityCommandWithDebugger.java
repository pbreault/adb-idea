package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.*;
import com.android.tools.idea.ddms.DevicePanel;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.intellij.execution.*;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.executors.DefaultDebugExecutor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.execution.remote.RemoteConfiguration;
import com.intellij.execution.remote.RemoteConfigurationType;
import com.intellij.execution.ui.*;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiClass;
import com.intellij.ui.content.Content;
import com.intellij.util.NotNullFunction;
import org.jetbrains.android.dom.AndroidDomUtil;
import org.jetbrains.android.dom.manifest.*;
import org.jetbrains.android.facet.AndroidFacet;
import org.jetbrains.android.logcat.AndroidLogcatView;
import org.jetbrains.android.logcat.AndroidToolWindowFactory;
import org.jetbrains.android.sdk.AndroidSdkUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

public class StartDefaultActivityCommandWithDebugger implements Command {
    public static final String LAUNCH_ACTION_NAME = "android.intent.action.MAIN";
    public static final String LAUNCH_CATEGORY_NAME = "android.intent.category.LAUNCHER";
    //private MyDebugLauncher debugLauncher;
    //private static Project prvProject;
    //public ProgramRunner runner;

    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        String defaultActivityName = getDefaultActivityName(facet);
        String component = packageName + "/" + defaultActivityName;

        try {
            StartActivityReceiver receiver = new StartActivityReceiver();
            device.executeShellCommand("am start -D -n " + component, receiver, 5L, TimeUnit.MINUTES);

            PropertiesComponent properties = PropertiesComponent.getInstance(project);

            boolean status = startDebugging(device, project, facet, packageName);
            if (!status) return false;

            if (receiver.isSuccess()) {
                info(String.format("<b>%s</b> started on %s", packageName, device.getName()));
                return true;
            } else {
                error(String.format("<b>%s</b> could not bet started on %s. \n\n<b>ADB Output:</b> \n%s", packageName, device.getName(), receiver.getMessage()));
            }
        } catch (Exception e) {
            error("Start fail... " + e.getMessage());
        }

        return false;
    }

    private String getDefaultActivityName(final AndroidFacet facet) {
        return ApplicationManager.getApplication().runReadAction(new Computable<String>() {
            @Override
            public String compute() {
                return getDefaultLauncherActivityName(facet.getManifest());
            }
        });
    }

    public static class StartActivityReceiver extends MultiLineReceiver {

        public String message = "Nothing Received";

        public List<String> currentLines = new ArrayList<String>();

        @Override
        public void processNewLines(String[] strings) {
            for (String s : strings) {
                if (!Strings.isNullOrEmpty(s)) {
                    currentLines.add(s);
                }
            }
            computeMessage();
        }

        private void computeMessage() {
            message = Joiner.on("\n").join(currentLines);
        }

        @Override
        public boolean isCancelled() {
            return false;
        }

        public String getMessage() {
            return message;
        }

        public boolean isSuccess() {
            return currentLines.size() > 0 && currentLines.size() < 3;
        }
    }

    // copied from AOSP since it changed between 0.4.3 and 0.4.4
    @Nullable
    public static String getDefaultLauncherActivityName(@NotNull Manifest manifest) {
        Application application = manifest.getApplication();
        if (application == null) {
            return null;
        }

        for (Activity activity : application.getActivities()) {
            for (IntentFilter filter : activity.getIntentFilters()) {
                if (AndroidDomUtil.containsAction(filter, LAUNCH_ACTION_NAME) && AndroidDomUtil.containsCategory(filter, LAUNCH_CATEGORY_NAME)) {
                    PsiClass c = activity.getActivityClass().getValue();
                    return c != null ? c.getQualifiedName() : null;
                }
            }
        }

        for (ActivityAlias alias : application.getActivityAliass()) {
            for (IntentFilter filter : alias.getIntentFilters()) {
                if (AndroidDomUtil.containsAction(filter, LAUNCH_ACTION_NAME) && AndroidDomUtil.containsCategory(filter, LAUNCH_CATEGORY_NAME)) {
                    return alias.getName().getStringValue();
                }
            }
        }

        return null;
    }


    private void closeOldSessionAndRun(String port, Project project) {
        final String configurationName = getRunConfigurationName(port);
        Collection descriptors = ExecutionHelper.findRunningConsoleByTitle(project, new NotNullFunction() {
            @NotNull
            public Boolean fun(String title) {
                Boolean status = Boolean.valueOf(configurationName.equals(title));
                if (status == null) throw new IllegalStateException(String.format("ERROR: parameters must not return null"));
                return status;
            }

            @NotNull
            @Override
            public Object fun(Object o) {
                if (o.getClass() == String.class) {
                    return fun((String)o);
                }
                return null;
            }
        });

        if (descriptors.size() > 0) {
            RunContentDescriptor descriptor = (RunContentDescriptor)descriptors.iterator().next();
            ProcessHandler processHandler = descriptor.getProcessHandler();
            Content content = descriptor.getAttachedContent();

            if ((processHandler != null) && (content != null)) {
                Executor executor = DefaultDebugExecutor.getDebugExecutorInstance();
                if (processHandler.isProcessTerminated()) {
                    ExecutionManager.getInstance(project).getContentManager().removeRunContent(executor, descriptor);
                } else {
                    content.getManager().setSelectedContent(content);
                    ToolWindow window = ToolWindowManager.getInstance(project).getToolWindow(executor.getToolWindowId());
                    window.activate(null, false, true);
                    return;
                }
            }
        }

        runSession(port, project);
    }


    private void runSession(String port, Project project) {
        RunnerAndConfigurationSettings settings = createRunConfiguration(project, port);
        ProgramRunnerUtil.executeConfiguration(project, settings, DefaultDebugExecutor.getDebugExecutorInstance());
    }


    @NotNull
    private static String getRunConfigurationName(String debugPort) {
        String info = String.format("Android Debugger (%s)", new Object[] { debugPort });
        if (info == null) throw new IllegalStateException(String.format("ERROR: parameters must not return null"));
        return info;
    }


    @NotNull
    private static RunnerAndConfigurationSettings createRunConfiguration(Project project, String debugPort) {
        RemoteConfigurationType remoteConfigurationType = RemoteConfigurationType.getInstance();

        if (remoteConfigurationType == null) {
            error("Cannot create remote configuration");
        }

        ConfigurationFactory factory = remoteConfigurationType.getFactory();
        RunnerAndConfigurationSettings runSettings = RunManager.getInstance(project).createRunConfiguration(getRunConfigurationName(debugPort), factory);

        RemoteConfiguration configuration = (RemoteConfiguration)runSettings.getConfiguration();

        configuration.HOST = "localhost";
        configuration.PORT = debugPort;
        configuration.USE_SOCKET_TRANSPORT = true;
        configuration.SERVER_MODE = false;

        return runSettings;
    }


    private boolean startDebugging(final IDevice device, final Project project, final AndroidFacet facet, final String packageName) {
        if (device == null) throw new IllegalArgumentException(String.format("ERROR: startDebugging(): device == null"));

        info(String.format("Target device: " + device.getName(), ProcessOutputTypes.STDOUT));
        try {
            AndroidDebugBridge bridge = AndroidDebugBridge.getBridge();
            if ((bridge != null) && (AndroidSdkUtils.canDdmsBeCorrupted(bridge))) {
                error(String.format("ERROR: ddms can be corrupted."));
                return false;
            }

            Client client = device.getClient(packageName);
            if (client == null) {
                error(String.format("ERROR: getClient() == null"));
                return false;
            }
            String port = Integer.toString(client.getDebuggerListenPort());
            closeOldSessionAndRun(port, project);

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                public void run() {
                    final ToolWindow androidToolWindow = ToolWindowManager.getInstance(project).getToolWindow(AndroidToolWindowFactory.TOOL_WINDOW_ID);
                    androidToolWindow.activate(new Runnable() {
                        public void run() {
                            int count = androidToolWindow.getContentManager().getContentCount();
                            for (int i = 0; i < count; i++) {
                                Content content = androidToolWindow.getContentManager().getContent(i);
                                DevicePanel devicePanel = content == null ? null : (DevicePanel)content.getUserData(AndroidToolWindowFactory.DEVICES_PANEL_KEY);
                                AndroidLogcatView logcatView = content == null ? null : (AndroidLogcatView)content.getUserData(AndroidLogcatView.ANDROID_LOGCAT_VIEW_KEY);
                                if (devicePanel != null) {
                                    devicePanel.selectDevice(device);
                                    if (logcatView == null) break;
                                    logcatView.createAndSelectFilterByPackage(packageName); break;
                                }
                            }
                        }
                    }, false);
                }
            });

            return true;
        } catch (Exception e) {
            error(String.format("ERROR: Exception!"));
        }

        return false;
    }
}
