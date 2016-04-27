package com.developerphil.adbidea.adb.command;

import com.android.ddmlib.IDevice;
import com.intellij.openapi.project.Project;
import org.jetbrains.android.facet.AndroidFacet;

import java.util.ArrayList;
import java.util.List;

public class CommandList implements Command {

    private List<Command> commands;

    public CommandList(Command... commands) {
        this.commands = new ArrayList<Command>();
        for (Command command : commands) {
            this.commands.add(command);
        }
    }

    @Override
    public boolean run(Project project, IDevice device, AndroidFacet facet, String packageName) {
        for (Command command : commands) {
            boolean success = command.run(project, device, facet, packageName);
            if (!success) {
                return false;
            }
        }

        return true;
    }


}
