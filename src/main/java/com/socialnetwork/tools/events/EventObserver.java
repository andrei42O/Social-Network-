package com.socialnetwork.tools.events;

import com.socialnetwork.domain.Event;
import com.socialnetwork.tools.observer.Observer;

public class EventObserver implements ObservableEvent {
    private Event event;

    public EventObserver(Event event) {
        this.event = event;
    }

    @Override
    public String getType() {
        return "event";
    }
}
