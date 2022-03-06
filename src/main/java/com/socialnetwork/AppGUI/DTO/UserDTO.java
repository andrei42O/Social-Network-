package com.socialnetwork.AppGUI.DTO;

import com.socialnetwork.domain.*;
import com.socialnetwork.service.GroupService;
import com.socialnetwork.service.MessageService;
import com.socialnetwork.service.ServiceFriendship;
import com.socialnetwork.service.UserService;
import javafx.scene.layout.HBox;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserDTO {
    private Long ID;
    private String firstName;
    private String lastName;
    private String userName;
    private ServiceFriendship friendshipService;
    private UserService userService;
    private MessageService messageService;
    private GroupService groupService;

    public UserDTO(User u, ServiceFriendship friendshipService, UserService userService, MessageService messageService, GroupService groupService) {
        this.ID = u.getId();
        this.firstName = u.getFirstName();
        this.lastName = u.getLastName();
        this.userName = u.getUserName();
        this.friendshipService = friendshipService;
        this.userService = userService;
        this.messageService = messageService;
        this.groupService = groupService;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<User> getFriends() {
        return friendshipService.getUserFriends(ID);
    }

    public List<Message> getMessages() {
        return StreamSupport.stream(messageService.getUserMessages(ID).spliterator(), false)
                .collect(Collectors.toList());
    }

    public List<FriendRequest> getFriendRequests() {
        return StreamSupport.stream(friendshipService.getUserFriendRequests(ID).spliterator(), false)
                .filter(fr -> {return fr.getState() == 0;}).collect(Collectors.toList());
    }

    public List<FriendRequest> getFriendRequestsHistory() {
        return StreamSupport.stream(friendshipService.getUserFriendRequests(ID).spliterator(), false)
                .filter(fr -> {return fr.getState() != 0;}).collect(Collectors.toList());
    }

    public Pair<List<User>, List<Group>> getChattingUsers() {
        Pair<List<User>, List<Group>> ret = new Pair<>(new ArrayList<>(), new ArrayList<>());
        Set<User> users = new HashSet<>();
        Set<Group> groups = new HashSet<>();
        StreamSupport.stream(messageService.getUserMessages(ID).spliterator(), false)
                .forEach(msg -> {
                   if(msg.getGroupID() == -1) {
                       if(!msg.selfConversation())
                            users.add(msg.getParticipants().stream()
                                                .filter(user -> { return !user.getId().equals(this.ID);})
                                                .collect(Collectors.toList()).get(0));
                   }
                   else{
                       groups.add(groupService.findGroup(msg.getGroupID()));
                   }
                });
        StreamSupport.stream(groupService.findAll().spliterator(), false).forEach(groups::add);
        return new Pair<List<User>, List<Group>>(new ArrayList<>(users), new ArrayList<>(groups));
    }

    public Long getID(){ return this.ID; }

    public Iterable<Message> getChat(Long otherUserID) throws Exception {
        return messageService.showConversation(ID, otherUserID);
    }

    public Iterable<Message> getGroupChat(Long id) {
        return messageService.getGroupMessages(id);
    }

    public List<Message> getMessagesIn(LocalDate start, LocalDate end) {
        return StreamSupport.stream(messageService.getUserMessages(ID).spliterator(), false)
                .filter(msg -> {
                    return msg.getDate().toLocalDate().isBefore(end) && msg.getDate().toLocalDate().isAfter(start);
                }).sorted(new Comparator<Message>() {
                    @Override
                    public int compare(Message o1, Message o2) {
                        if (o1.getDate().isBefore(o2.getDate()))
                            return 0;
                        return 1;
                    }
                }).collect(Collectors.toList());
    }

    public List<FriendRequest> getFriendRequests(LocalDate start, LocalDate end) {
        return StreamSupport.stream(friendshipService.findAll_friendrequests().spliterator(), false)
                .filter(fr -> {
                    return fr.getDate().isAfter(start) && fr.getDate().isBefore(end);
                }).sorted(new Comparator<FriendRequest>(){

                    @Override
                    public int compare(FriendRequest o1, FriendRequest o2) {
                        if(o1.getDate().isBefore(o2.getDate()))
                            return 0;
                        return 1;
                    }
                }).collect(Collectors.toList());
    }

    public List<Friendship> getFriendships(LocalDate start, LocalDate end) {
        try {
            return StreamSupport.stream(friendshipService.getFriends(ID).spliterator(), false)
                    .filter(fr -> {
                        return fr.getSecond().isAfter(start) && fr.getSecond().isBefore(end);
                    })
                    .map(fr -> {return new Friendship(ID, fr.getFirst().getId(),fr.getSecond());})
                    .sorted(new Comparator<Friendship>(){

                        @Override
                        public int compare(Friendship o1, Friendship o2) {
                            if(o1.getDate().isBefore(o2.getDate()))
                                return 0;
                            return 1;
                        }
                    }).collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<Friendship>();
        }
    }
}
