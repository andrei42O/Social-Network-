package com.socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Message extends Entity<Long>{
    private User from;
    private Set<User> to = null;
    private String message;
    private Message reply;
    private LocalDateTime date;
    private Long groupID = -1L;

    /**
     * Constructors
     * @param from
     * @param to
     * @param message
     */
    public Message(User from, Set<User> to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.reply = null;
        this.date = LocalDateTime.now();
        this.to.add(from); // the user can message himself and see the messages he sent
    }
    public Message(User from, Set<User> to, String message, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.reply = null;
        this.date = date;
        this.to.add(from); // the user can message himself and see the messages he sent
    }
    public Message(User from, Set<User> to, String message, Message reply, LocalDateTime date) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.reply = reply;
        this.date = date;
        this.to.add(from); // the user can message himself and see the messages he sent
    }
    public Message(User from, Set<User> to, String message, Message reply) {
        this.from = from;
        this.to = to;
        this.message = message;
        this.reply = reply;
        this.date = LocalDateTime.now();
        this.to.add(from); // the user can message himself and see the messages he sent
    }

    public Message(User from, Long groupId, String message, Message reply){
        this.from = from;
        this.message = message;
        this.reply = reply;
        this.date = LocalDateTime.now();
        this.groupID = groupId;
    }

    public Message(User from, Long groupId, String message, Message reply, LocalDateTime date){
        this.from = from;
        this.message = message;
        this.reply = reply;
        this.date = date;
        this.groupID = groupId;
    }

    /**
     * Get from user
     * @return
     */
    public User getFrom() {
        return from;
    }

    /**
     * Set from user
     * @param from new user
     */
    public void setFrom(User from) {
        this.from = from;
    }

    /**
     * check if a chat is private or not
     * @return
     */
    public boolean checkIfPrivateChat(){
        return to.size() == 1;
    }

    /**
     * Get set of uesrs
     * @return
     */
    public Set<User> getTo() {
        return to;
    }

    /**
     * Set set of users
     * @param to new set
     */
    public void setTo(Set<User> to) {
        this.to = to;
    }

    /**
     * get messages
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * set message
     * @param message new message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * get reply
     * @return
     */
    public Message getReply() {
        return reply;
    }

    /**
     * set reply
     * @param reply new reply
     */
    public void setReply(Message reply) {
        this.reply = reply;
    }

    /**
     * add to user
     * @param u
     */
    public void addTo(User u){
        to.add(u);
    }

    /**
     * get date
     * @return
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * set date
     * @param date new date
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean sameConversationAs(Message msg){
        return this.getParticipants().equals(msg.getParticipants());
    }

    /**
     * override hashmap and equals
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        if (!(o instanceof Message)) return false;
        Message that = (Message) o;
        return getMessage().equals(that.getMessage()) &&
                getFrom().equals(that.getFrom()) &&
                Objects.equals(getReply(), that.getReply()) &&
                getTo().equals(that.getTo()) &&
                getDate().equals(that.getDate());
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    /**
     * override to string
     * @return
     */
    @Override
    public String toString() {
        String ret ="Message #" + this.getId() +
                "\nDate: " + "(" + date + ")" +
                "\nFrom: " + from +
                "\nTo: " + to;
        if(reply != null)
            ret += "Replied to message ID: " + reply.getId();
        ret += "\nContent\n\"" + message + "\"\n";
        return ret;
    }

    /**
     * get participants
     * @return
     */
    public Set<User> getParticipants() {
        Set<User> ret = to;
        ret.add(from);
        return ret;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public Long getGroupID() {
        return groupID;
    }

    public boolean selfConversation(){
        return to.size() == 1 && to.contains(from);
    }

    public boolean sameParticipants(Long... ids) {
        if(ids.length != to.size()) // from
            return false;
        var temp = to.stream().map(Entity::getId).collect(Collectors.toSet());
        for (Long id : ids) {
            if(!temp.contains(id))
                return false;
        }
        return true;
    }
}
