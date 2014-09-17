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
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.developerphil.adbidea.ui.NotificationHelper.error;
import static com.developerphil.adbidea.ui.NotificationHelper.info;

public class StartDefaultActivityCommandWithDebugger implements Command {
    public static final String LAUNCH_ACTION_NAME = "android.intent.action.MAIN";
    public static final String LAUNCH_CATEGORY_NAME = "android.intent.category.LAUNCHER";
    private ToolWindow androidToolWindow;
    private String prvPackageName;
    private RunnerAndConfigurationSettings prvSettings;
    private Project prvProject;

    @Override
    public boolean run(final Project project, final IDevice device, final AndroidFacet facet, final String packageName) {
        String defaultActivityName = getDefaultActivityName(facet);
        String component = packageName + "/" + defaultActivityName;

        try {
            StartActivityReceiver receiver = new StartActivityReceiver();
            device.executeShellCommand("am start -D -n " + component, receiver, 5L, TimeUnit.MINUTES);

            boolean status = startDebugging(device, project, facet, packageName);
            if (!status) {
                error(String.format("startDebugging returns false."));
            }

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
        final String configurationName = String.format("Android Debugger (%s)", new Object[]{port});
        prvProject = project;
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

        RemoteConfigurationType remoteConfigurationType = RemoteConfigurationType.getInstance();

        if (remoteConfigurationType == null) {
            error("Cannot create remote configuration");
        }

        ConfigurationFactory factory = remoteConfigurationType.getFactory();
        prvSettings = RunManager.getInstance(project).createRunConfiguration(configurationName, factory);

        RemoteConfiguration configuration = (RemoteConfiguration)prvSettings.getConfiguration();
        configuration.HOST = "localhost";
        configuration.PORT = port;
        configuration.USE_SOCKET_TRANSPORT = true;
        configuration.SERVER_MODE = false;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ProgramRunnerUtil.executeConfiguration(prvProject, prvSettings, DefaultDebugExecutor.getDebugExecutorInstance());
            }
        });
    }


    private boolean startDebugging(final IDevice device, final Project project, final AndroidFacet facet, final String packageName) {
        if (device == null) throw new IllegalArgumentException(String.format("ERROR: startDebugging(): device == null"));
        prvPackageName = packageName;
        info(String.format("Target device: " + device.getName(), ProcessOutputTypes.STDOUT));
        try {
            AndroidDebugBridge bridge = AndroidDebugBridge.getBridge();
            if ((bridge != null) && (AndroidSdkUtils.canDdmsBeCorrupted(bridge))) {
                error(String.format("ERROR: ddms can be corrupted."));
                return false;
            }

            if (device.isOnline()) {
                info(String.format("INFO: isOnline"));
            }

            if (device.isOffline()) {
                info(String.format("INFO: isOffline"));
            }

            if (device.isEmulator()) {
                info(String.format("INFO: isEmulator"));
            }

            if (device.isBootLoader()) {
                info(String.format("INFO: isBootLoader"));
            }

            Client clients[];
            Client client = null;
            if (device.hasClients()) {
                info(String.format("INFO: hasClients"));
                clients = device.getClients();
                info(String.format("INFO: found %d clients", clients.length));
                for (int i=0; i<clients.length; i++) {
                    if (packageName.equalsIgnoreCase(clients[i].getClientData().getClientDescription())) {
                        info(String.format("INFO: found package client"));
                        client = clients[i];
                        break;
                    }
                }
            } else {
                error(String.format("ERROR: hasClients == false"));
                return false;
            }

            if (client == null) {
                client = device.getClient(packageName);
            }

            if (client == null) {
                error(String.format("ERROR: client == null"));
                return false;
            }

            if (client.isDebuggerAttached()) {
                info(String.format("INFO: isDebuggerAttached[TRUE]"));
            }
            String port = Integer.toString(client.getDebuggerListenPort());
            closeOldSessionAndRun(port, project);

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                public void run() {
                    androidToolWindow = ToolWindowManager.getInstance(project).getToolWindow(AndroidToolWindowFactory.TOOL_WINDOW_ID);
                    androidToolWindow.activate(new Runnable() {
                        public void run() {
                            int count = androidToolWindow.getContentManager().getContentCount();
                            for (int i = 0; i < count; i++) {
                                Content content = androidToolWindow.getContentManager().getContent(i);
                                DevicePanel devicePanel = content == null ? null : (DevicePanel)content.getUserData(AndroidToolWindowFactory.DEVICES_PANEL_KEY);
                                AndroidLogcatView logcatView = content == null ? null : (AndroidLogcatView)content.getUserData(AndroidLogcatView.ANDROID_LOGCAT_VIEW_KEY);
                                if (devicePanel != null) {
                                    devicePanel.selectDevice(device);
                                    if (logcatView == null) {
                                        break;
                                    }
                                    logcatView.createAndSelectFilterByPackage(prvPackageName);
                                    break;
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