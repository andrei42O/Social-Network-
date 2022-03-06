package com.socialnetwork.domain.notifications;

import com.socialnetwork.AppGUI.utils.NotificationType;
import com.socialnetwork.domain.User;

public class FriendshipNotification implements Notification{
    private User u1;
    private User u2;

    public FriendshipNotification(User u1, User u2) {
        this.u1 = u1;
        this.u2 = u2;
    }

    @Override
    public String getNotification() {
        return null;
    }

    @Override
    public NotificationType getType() {
        return null;
    }
}
