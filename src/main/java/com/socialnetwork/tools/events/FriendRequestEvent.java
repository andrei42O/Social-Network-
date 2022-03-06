package com.socialnetwork.tools.events;

import com.socialnetwork.domain.FriendRequest;

public class FriendRequestEvent implements ObservableEvent{
    FriendRequest fr;

    public FriendRequestEvent(FriendRequest fr) {
        this.fr = fr;
    }

    @Override
    public String getType() {
        return "friendrequest";
    }

    public FriendRequest getFr() {
        return fr;
    }

}
