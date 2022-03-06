package com.socialnetwork.domain;

import com.socialnetwork.AppGUI.utils.EventType;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Event extends Entity<Long>{
    private EventType type;
    private String name;
    private String description;

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    private LocalDate date;
    private HashMap<Long, Boolean> participants = new HashMap<>(){};

    public Event(String name, EventType type, LocalDate date, String description) {
        this.type = type;
        this.name = name;
        this.date = date;
        this.description = description;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<Long> getParticipants() {
        return participants.keySet();
    }

    public void setParticipants(Set<Long> asd) {
        asd.forEach(id -> {
            participants.put(id, Boolean.TRUE);
        });
    }

    public void addParticipant(Long participantID){
        participants.put(participantID, Boolean.TRUE);
    }

    public void removeParticipant(Long participantID){
        participants.remove(participantID);
    }

    public boolean going(Long id) {
        Boolean ans = participants.get(id);
        return ans != null && ans;
    }
}
