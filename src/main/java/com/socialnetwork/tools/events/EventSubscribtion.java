package com.socialnetwork.tools.events;

import com.socialnetwork.domain.notifications.Notification;

public class EventSubscribtion implements ObservableEvent{
    Notification notification;

    public EventSubscribtion(Notification notification) {
        this.notification = notification;
    }
    public Notification getNotification() {
        return notification;
    }

    @Override
    public String getType() {
        return "notification";
    }
}
