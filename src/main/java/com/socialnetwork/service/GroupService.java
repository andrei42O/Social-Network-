package com.socialnetwork.service;

import com.socialnetwork.domain.Group;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.exceptions.NotFoundException;
import com.socialnetwork.repository.paging.PagingRepository;
import com.socialnetwork.tools.events.ObservableEvent;
import com.socialnetwork.tools.events.GroupObservableEvent;
import com.socialnetwork.tools.observer.ThreadSafeObservable;

import java.util.List;

public class GroupService extends ThreadSafeObservable<ObservableEvent> {
    private PagingRepository<Long, Group> groups;
    private PagingRepository<Long, User> users;

    public GroupService(PagingRepository<Long, Group> groups, PagingRepository<Long, User> users) {
        this.groups = groups;
        this.users = users;
    }

    public Group findGroup(Long id){
        return groups.findOne(id);
    }

    public void addMember(Long userID, Long groupID) throws Exception {
        Group group = groups.findOne(groupID);
        if(group == null)
            throw new NotFoundException("Group #" + groupID + " not found!\n");
        User user = users.findOne(userID);
        if(user == null)
            throw new NotFoundException("User #" + userID + " not found!\n");
        List<Long> ids = group.getUsers();
        ids.add(user.getId());
        group.setUsers(ids);
        if(groups.update(group) != null)
            throw new Exception("The user could not be added tot the group!\n");
    }

    public void removeMember(Long userID, Long groupID) throws Exception {
        Group group = groups.findOne(groupID);
        if(group == null)
            throw new NotFoundException("Group #" + groupID + " not found!\n");
        User user = users.findOne(userID);
        if(user == null)
            throw new NotFoundException("User #" + userID + " not found!\n");
        List<Long> ids = group.getUsers();
        ids.remove(user.getId());
        group.setUsers(ids);
        if(groups.update(group) != null)
            throw  new Exception("The user could not be deleted from the group!\n");
    }

    public void changeGroupName(String groupName, Long groupID) throws Exception {
        Group group = groups.findOne(groupID);
        if(group == null)
            throw new NotFoundException("Group #" + groupID + " not found!\n");
        group.setGroupName(groupName);
        if(groups.update(group) != null)
            throw  new Exception("The user could not be deleted from the group!\n");
    }

    public void createGroup(String groupName, Long... userIDS) throws Exception {
        for(Long id: userIDS){
            if(users.findOne(id) == null)
                throw new Exception("The group couldn't be created because not all users exist!");
        }
        Group group = new Group(groupName, userIDS);
        if(groups.save(group) != null)
            throw new Exception("The group couldn't be created!");
    }
    public void createGroup(String groupName, List<Long> userIDS) throws Exception {
        for(Long id: userIDS){
            if(users.findOne(id) == null)
                throw new Exception("The group couldn't be created because not all users exist!");
        }
        Group group = new Group(groupName, userIDS);
        if(groups.save(group) != null)
            throw new Exception("The group couldn't be created!");
        notifyObservers(new GroupObservableEvent(group));
    }

    public Iterable<Group> findAll(){
        return groups.findAll();
    }
}
