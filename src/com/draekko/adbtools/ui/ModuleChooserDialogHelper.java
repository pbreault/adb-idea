package com.draekko.adbtools.ui;

import com.google.common.collect.Lists;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ui.configuration.ChooseModulesDialog;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.List;

public final class ModuleChooserDialogHelper {

    public static final String SELECTED_MODULE_PROPERTY = ModuleChooserDialogHelper.class.getCanonicalName() + "-SELECTED_MODULE";

    private ModuleChooserDialogHelper() {
    }

    public static AndroidFacet showDialogForFacets(Project project, List<AndroidFacet> facets) {
        List<Module> modules = Lists.newArrayList();
        String previousModuleName = getPreviousModuleName(project);
        List<Module> previousSelectedModule = null;
        for (AndroidFacet facet : facets) {
            Module module = facet.getModule();
            modules.add(module);
            if (module.getName().equals(previousModuleName)) {
                previousSelectedModule = Lists.newArrayList(module);
            }
        }

        ChooseModulesDialog dialog = new ChooseModulesDialog(project, modules, "Choose Module", "");
        dialog.setSingleSelectionMode();
        if (previousSelectedModule != null) {
            dialog.selectElements(previousSelectedModule);
        }
        dialog.show();

        List<Module> chosenElements = dialog.getChosenElements();
        if (chosenElements.isEmpty()) {
            return null;
        }

        Module chosenModule = chosenElements.get(0);
        saveModuleName(project, chosenModule.getName());
        int chosenModuleIndex = modules.indexOf(chosenModule);
        return facets.get(chosenModuleIndex);
    }

    private static void saveModuleName(Project project, String moduleName) {
        final PropertiesComponent properties = PropertiesComponent.getInstance(project);
        properties.setValue(SELECTED_MODULE_PROPERTY, moduleName);
    }

    private static String getPreviousModuleName(Project project) {
        final PropertiesComponent properties = PropertiesComponent.getInstance(project);
        return properties.getValue(SELECTED_MODULE_PROPERTY);
    }

}
