package com.socialnetwork.tools.events;

import com.socialnetwork.domain.Group;

public class GroupObservableEvent implements ObservableEvent {
    private Group group;

    public GroupObservableEvent(Group group) {
        this.group = group;
    }

    public Group getGroup(){
        return group;
    }

    @Override
    public String getType() {
        return "groupEvent";
    }
}
