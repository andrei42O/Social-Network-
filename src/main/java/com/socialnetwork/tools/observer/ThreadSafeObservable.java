package com.socialnetwork.tools.observer;

import com.socialnetwork.tools.events.ObservableEvent;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadSafeObservable<E extends ObservableEvent> {
    // Can use CopyOnWriteArraySet too
    private final Set<Observer<E>> mObservers = Collections.newSetFromMap(
            new ConcurrentHashMap<Observer<E>, Boolean>(0));
    /**
     * This method adds a new Observer - it will be notified when Observable changes
     */
    public void registerObserver(Observer<E> observer) {
        if (observer == null) return;
        mObservers.add(observer); // this is safe due to thread-safe Set
    }
    /**
     * This method removes an Observer - it will no longer be notified when Observable changes
     */
    public void unregisterObserver(Observer<E> observer) {
        if (observer != null) {
            mObservers.remove(observer); // this is safe due to thread-safe Set
        }
    }
    /**
     * This method notifies currently registered observers about Observable's change
     */
    public void notifyObservers(E e) {
        for (Observer<E> observer : mObservers) { // this is safe due to thread-safe Set
            observer.update(e);
        }
    }
}
