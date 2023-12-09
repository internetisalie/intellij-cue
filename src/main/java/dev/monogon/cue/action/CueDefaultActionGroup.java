package dev.monogon.cue.action;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import dev.monogon.cue.Icons;
import org.jetbrains.annotations.NotNull;

/**
 * Creates an action group to contain menu actions. See plugin.xml declarations.
 */
public class CueDefaultActionGroup extends DefaultActionGroup {

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }

    /**
     * Given {@link CueDefaultActionGroup} is derived from {@link com.intellij.openapi.actionSystem.ActionGroup},
     * in this context {@code update()} determines whether the action group itself should be enabled or disabled.
     * Requires an editor to be active in order to enable the group functionality.
     *
     * @param event Event received when the associated group-id menu is chosen.
     */
    @Override
    public void update(AnActionEvent event) {
        // Enable/disable depending on whether user is editing
        //Editor editor = event.getData(CommonDataKeys.EDITOR);
        event.getPresentation().setEnabled(true);
        // Take this opportunity to set an icon for the group.
        event.getPresentation().setIcon(Icons.CueLogo);
    }
}