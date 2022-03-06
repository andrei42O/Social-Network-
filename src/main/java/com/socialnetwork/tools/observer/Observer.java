package com.socialnetwork.tools.observer;

import com.socialnetwork.tools.events.ObservableEvent;

public interface Observer<E extends ObservableEvent> {
    void update(E e);
}