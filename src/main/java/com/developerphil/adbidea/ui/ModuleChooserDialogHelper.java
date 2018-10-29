package com.developerphil.adbidea.ui;

import com.google.common.collect.Lists;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import java.util.Collections;
import java.util.List;
import org.jetbrains.android.facet.AndroidFacet;

public final class ModuleChooserDialogHelper {

    public static final String SELECTED_MODULE_PROPERTY = ModuleChooserDialogHelper.class.getCanonicalName() + "-SELECTED_MODULE";
    public static final String DEFAULT_MODULE_PROPERTY = ModuleChooserDialogHelper.class.getCanonicalName() + "-DEFAULT_MODULE";

    public static final String DO_NOT_SELECT_THE_DEFAULT_MODULE = "Do not select the default module";


    private ModuleChooserDialogHelper() {
    }

    public static AndroidFacet showDialogForFacets(Project project, List<AndroidFacet> facets,boolean isSetDefault) {
        List<Module> modules = Lists.newArrayList();
        List<String> modulesName = Lists.newArrayList();
        String previousModuleName = getPreviousModuleName(project);
        String defaultModuleName = getDefaultModuleName(project);
        List<String> previousSelectedModule = null;
        for (AndroidFacet facet : facets) {
            Module module = facet.getModule();
            String name = module.getName();
            if (!isSetDefault && name.equals(defaultModuleName)) {
                return facet;
            }
            modules.add(module);
            modulesName.add(name);
            if (name.equals(previousModuleName)) {
                previousSelectedModule = Lists.newArrayList(name);
            } else if (isSetDefault && name.equals(defaultModuleName)) {
                previousSelectedModule = Lists.newArrayList(name);
            }
        }

        if (isSetDefault && previousSelectedModule == null) {
            previousSelectedModule = Lists.newArrayList(DO_NOT_SELECT_THE_DEFAULT_MODULE);
        }

        Collections.sort(modules, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        Collections.sort(modulesName, String :: compareToIgnoreCase);
        if (isSetDefault) {
            modulesName.add(DO_NOT_SELECT_THE_DEFAULT_MODULE);
        }
        String titleSelectDefault = "Choose Default Module";
        if (!Utils.isEmpty(defaultModuleName)) {
            titleSelectDefault += ",Current module is :" + defaultModuleName;
        }
        MyChooseModulesDialog dialog = new MyChooseModulesDialog(project, modulesName, isSetDefault? titleSelectDefault :"Choose Module", isSetDefault?"Set the default module for each operation":"", ModuleType.get(modules.get(0)).getIcon());
        dialog.setSingleSelectionMode();
        if (previousSelectedModule != null) {
            dialog.selectElements(previousSelectedModule);
        }
        dialog.show();

        List<String> chosenElements = dialog.getChosenElements();
        if (chosenElements.isEmpty()) {
            return null;
        }

        String chosenModule = chosenElements.get(0);
        if (isSetDefault) {
            if (chosenModule.equals(DO_NOT_SELECT_THE_DEFAULT_MODULE)) {
                saveDefaultModuleName(project,"");
                return null;
            } else {
                saveDefaultModuleName(project,chosenModule);
            }
        } else {
            saveModuleName(project, chosenModule);
        }
        int chosenModuleIndex = modulesName.indexOf(chosenModule);
        return facets.get(chosenModuleIndex);
    }

    private static void saveDefaultModuleName(Project project, String moduleName) {
        final PropertiesComponent properties = PropertiesComponent.getInstance(project);
        properties.setValue(DEFAULT_MODULE_PROPERTY, moduleName);
    }

    private static void saveModuleName(Project project, String moduleName) {
        final PropertiesComponent properties = PropertiesComponent.getInstance(project);
        properties.setValue(SELECTED_MODULE_PROPERTY, moduleName);
    }

    private static String getPreviousModuleName(Project project) {
        final PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(SELECTED_MODULE_PROPERTY);
    }

    private static String getDefaultModuleName(Project project) {
        final PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(DEFAULT_MODULE_PROPERTY);
    }


}
