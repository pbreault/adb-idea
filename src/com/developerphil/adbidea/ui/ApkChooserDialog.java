package com.developerphil.adbidea.ui;

import com.intellij.ide.util.ChooseElementsDialog;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.util.AndroidBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class ApkChooserDialog extends ChooseElementsDialog<File> {
    public ApkChooserDialog(Project project, List<? extends File> items) {
        super(project, items, "Destination Apk", AndroidBundle.message("android.extract.package.choose.dest.apk"));
        this.myChooser.setSingleSelectionMode();
    }

    @Override
    protected String getItemText(File file) {
        return file.getName();
    }

    @Nullable
    @Override
    protected Icon getItemIcon(File file) {
        return ModuleType.EMPTY.getIcon();
    }


}