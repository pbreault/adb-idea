package com.developerphil.adbidea.ui;

import com.intellij.icons.AllIcons;
import com.intellij.ide.util.ChooseElementsDialog;
import com.intellij.openapi.project.Project;
import java.util.List;
import javax.swing.Icon;
import org.jetbrains.annotations.Nullable;

public class MyChooseModulesDialog extends ChooseElementsDialog<String> {

    private Icon mIcon;

    public MyChooseModulesDialog(Project project, List<? extends String> items, String title, String description, Icon icon) {
        super(project, items, title, description, false);
        mIcon = icon;
    }

    public void setSingleSelectionMode() {
        this.myChooser.setSingleSelectionMode();
    }

    @Override
    protected String getItemText(String s) {
        return s;
    }

    @Nullable
    @Override
    protected Icon getItemIcon(String s) {
        return s.equals(ModuleChooserDialogHelper.DO_NOT_SELECT_THE_DEFAULT_MODULE) ? AllIcons.Actions.Clear: mIcon;
    }
}