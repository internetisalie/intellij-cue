package dev.monogon.cue.lang;

import com.intellij.lang.Language;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.openapi.util.NlsSafe;
import dev.monogon.cue.Messages;
import org.jetbrains.annotations.NotNull;

public class CueLanguage extends Language {
    public static final CueLanguage INSTANCE = new CueLanguage();
    public static final String NOTIFICATION_GROUP_ID = "CUE";
    public static final NotificationGroup NOTIFICATION_GROUP =
        NotificationGroupManager.getInstance().getNotificationGroup(NOTIFICATION_GROUP_ID);


    private CueLanguage() {
        super("CUE", "application/x-cuelang");
    }

    @Override
    public @NotNull @NlsSafe String getDisplayName() {
        return Messages.get("lang.displayName");
    }
}
