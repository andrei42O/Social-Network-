package com.socialnetwork.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Group extends Entity<Long> {
    private String groupName;
    private List<Long> users;

    public Group(String groupName, List<Long> users) {
        this.groupName = groupName;
        this.users = users;
    }

    public Group(String groupName, Long... ids){
        this.groupName = groupName;
        this.users = Arrays.stream(ids).collect(Collectors.toList());
    }

    public Group(String groupName) {
        this.groupName = groupName;
        this.users = new ArrayList<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<Long> getUsers() {
        return users;
    }

    public void setUsers(List<Long> users) {
        this.users = users;
    }

}
