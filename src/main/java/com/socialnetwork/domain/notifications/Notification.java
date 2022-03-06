package com.socialnetwork.domain.notifications;

import com.socialnetwork.AppGUI.utils.NotificationType;

public interface Notification {
    String getNotification();
    NotificationType getType();
}
