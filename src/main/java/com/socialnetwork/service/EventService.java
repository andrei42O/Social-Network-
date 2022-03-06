package com.socialnetwork.service;

import com.socialnetwork.AppGUI.utils.EventType;
import com.socialnetwork.domain.Event;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.notifications.EventNotification;
import com.socialnetwork.domain.notifications.Notification;
import com.socialnetwork.repository.db.EventRepository;
import com.socialnetwork.repository.db.UserDbRepository;
import com.socialnetwork.repository.paging.Page;
import com.socialnetwork.repository.paging.Pageable;
import com.socialnetwork.tools.events.EventObserver;
import com.socialnetwork.tools.events.EventSubscribtion;
import com.socialnetwork.tools.events.ObservableEvent;
import com.socialnetwork.tools.observer.Observable;
import com.socialnetwork.tools.observer.ThreadSafeObservable;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class EventService extends ThreadSafeObservable<ObservableEvent> {
    private UserDbRepository users;
    private EventRepository events;

    public EventService(UserDbRepository users, EventRepository eventRepository) {
        this.users = users;
        this.events = eventRepository;
    }

    public Event createEvent(String eventName, EventType type, LocalDate date, String description){
        Event event =  new Event(eventName, type, date, description);
        if(events.save(event) == null) {
            notifyObservers(new EventObserver(event));
            return event;
        }
        return null;
    }

    public Event addParticipant(Long eventID, Long userID){
        Event event = events.findOne(eventID);
        event.addParticipant(userID);
        if(events.update(event) == null) {
            notifyObservers(new EventSubscribtion(new EventNotification(event)));
            return event;
        }
        return null;
    }

    public Event removeParticipant(Long eventID, Long userID){
        Event event = events.findOne(eventID);
        event.removeParticipant(userID);
        if(events.update(event) == null)
            return event;
        return null;
    }

    public Page<Event> findAll(Pageable page){
        return events.findAll(page);
    }

    public int getNoOfEvents() {
        return events.getNoOfEvents();
    }

    public List<Notification> getNotifications(Long userID) {
        return StreamSupport.stream(events.findAll().spliterator(), false)
                .filter(event -> {return event.going(userID) && event.getDate().atTime(0,0,0).equals(LocalDate.now().plusDays(1).atTime(0,0,0));})
                .map(EventNotification::new)
                .collect(Collectors.toList());
    }
}
