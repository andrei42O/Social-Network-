package com.socialnetwork.domain.notifications;

import com.socialnetwork.AppGUI.utils.NotificationType;
import com.socialnetwork.domain.Event;

public class EventNotification implements Notification{
    private Event event;
    public EventNotification(Event event) {
        this.event = event;
    }

    @Override
    public String getNotification() {
        return String.format("Hurry up! Evenimentul %s cu tematica de %s va fi maine!", event.getName(), event.getType().toString());
    }

    @Override
    public NotificationType getType() {
        return null;
    }
}
