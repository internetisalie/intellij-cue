package dev.monogon.cue;

import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class Notifications {
    public static final String NOTIFICATION_GROUP_ID = "CUE";

    public static final NotificationGroup NOTIFICATION_GROUP =
        NotificationGroupManager.getInstance().getNotificationGroup(NOTIFICATION_GROUP_ID);


    public static void error(Project project, String message) {
        NOTIFICATION_GROUP
            .createNotification(message, NotificationType.ERROR)
            .notify(project);
    }

    public static void info(Project project, String message) {
        NOTIFICATION_GROUP
            .createNotification(message, NotificationType.INFORMATION)
            .notify(project);
    }

}
