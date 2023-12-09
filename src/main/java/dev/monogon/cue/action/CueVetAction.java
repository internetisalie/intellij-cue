package dev.monogon.cue.action;

import com.intellij.execution.ExecutionException;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import dev.monogon.cue.Messages;
import dev.monogon.cue.Notifications;
import dev.monogon.cue.cli.CueCommandFactory;
import dev.monogon.cue.cli.CueCommandService;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CueVetAction extends AnAction {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    @Override
    public void update(@NotNull AnActionEvent event) {
        final VirtualFile virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE);
        event.getPresentation().setEnabled(virtualFile != null);
        if (virtualFile == null) {
            return;
        }
        if (virtualFile.isDirectory()) {
            event.getPresentation().setText(Messages.get("action.directory.CueVetAction.text"));
            event.getPresentation().setDescription(Messages.get("action.directory.CueVetAction.description"));
        } else {
            event.getPresentation().setText(Messages.get("action.file.CueVetAction.text"));
            event.getPresentation().setDescription(Messages.get("action.file.CueVetAction.description"));
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        var target = event.getData(CommonDataKeys.VIRTUAL_FILE);
        if (target == null) {
            return;
        }

        var path = target.getPath();
        if (target.isDirectory()) {
            path = "./...";
        }

        try {
            var cmd = CueCommandFactory.getInstance().createCueVetCommand(List.of(path));
            if (target.isDirectory()) {
                cmd.setWorkDirectory(target.getPath());
            }
            CueCommandService.getInstance().executeForeground(event.getDataContext(), "cue vet", cmd);
        }
        catch (ExecutionException e) {
            Notifications.error(event.getProject(), e.getMessage());
        }
    }
}
