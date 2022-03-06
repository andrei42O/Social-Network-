package com.socialnetwork.models;

import com.socialnetwork.domain.Message;
import com.socialnetwork.domain.User;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;
import java.util.Set;

public class MessageModel {

        private final long hiddenID;
        private final User from;
        private final Set<User> to;
        private final SimpleStringProperty messageContent;
        private final Message reply;
        private final LocalDateTime date;

    public MessageModel(long hiddenID, User from, Set<User> to, String messageContent, Message reply, LocalDateTime date) {
        this.hiddenID = hiddenID;
        this.from = from;
        this.to = to;
        this.messageContent = new SimpleStringProperty(messageContent);
        this.reply = reply;
        this.date = date;
    }

    public MessageModel(Message msg){
        this.hiddenID = msg.getId();
        this.from = msg.getFrom();
        this.to = msg.getTo();
        this.messageContent = new SimpleStringProperty(msg.getMessage());
        this.reply = msg.getReply();
        this.date = msg.getDate();
    }


    public User getFrom(){ return from; }
    public Set<User> getTo(){ return to; }
    public Message getReply(){return reply;}
    public String getMessageContent() { return messageContent.get();}
    public LocalDateTime getDate(){return date;}
    public long getHiddenID(){ return hiddenID; }

    @Override
    public String toString() {
        if(reply == null)
            return String.format("Date %s\nFrom: %s\n%s\n", date, from.getUserName(), messageContent.get());
        return String.format("Date %s\nFrom: %s\nReplied to:\n%s\n%s\n", date, from.getUserName(), getShiftedMessage(reply, 4), messageContent.get());
    }

    private String getShiftedMessage(Message msg, int tabsize) {
        String tab = " ".repeat(tabsize);
        String ret = tab + "Date: " + msg.getDate() + "\n";
        ret += tab + "From: " + msg.getFrom().getUserName() + "\n";
        ret += tab + msg.getMessage();
        return ret;
    }
}
