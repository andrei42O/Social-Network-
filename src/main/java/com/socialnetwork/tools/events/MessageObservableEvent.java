package com.socialnetwork.tools.events;

import com.socialnetwork.domain.Message;

public class MessageObservableEvent implements ObservableEvent {
    private Message msg;
    public MessageObservableEvent(Message msg){
        this.msg = msg;
    }

    public Message getMessage() { return msg; }

    @Override
    public String getType() {
        return "messageEvent";
    }
}
