package com.socialnetwork.tools.observer;

import com.socialnetwork.tools.events.ObservableEvent;

public interface Observable<E extends ObservableEvent> {
    void addObserver(Observer<E> e);
    void removeObserver(Observer<E> e);
    void notifyObservers(E t);
}
