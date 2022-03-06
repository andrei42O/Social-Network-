package com.socialnetwork.service;

import com.socialnetwork.domain.Entity;
import com.socialnetwork.domain.Group;
import com.socialnetwork.domain.Message;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.exceptions.UserNotFoundException;
import com.socialnetwork.repository.paging.PagingRepository;
import com.socialnetwork.tools.events.ObservableEvent;
import com.socialnetwork.tools.events.MessageObservableEvent;
import com.socialnetwork.tools.observer.ThreadSafeObservable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageService extends ThreadSafeObservable<ObservableEvent> {
    private PagingRepository<Long, Message> messages;
    private PagingRepository<Long, User> users;
    private PagingRepository<Long, Group> groups;

    public MessageService(PagingRepository<Long, Message> messages, PagingRepository<Long, User> users, PagingRepository<Long, Group> groups){
        this.messages = messages;
        this.users = users;
        this.groups = groups;
    }

    public Iterable<Message> showConversation(Long groupID){
        Predicate<Message> sameGroup = msg -> {
            return msg.getGroupID().equals(groupID);
        };
        return StreamSupport.stream(messages.findAll().spliterator(), false)
                .filter(sameGroup)
                .collect(Collectors.toList());
    }


    public Iterable<Message> showConversation(Long... userIDS) throws Exception {
        List<Message> mess = new ArrayList<>();
        Set<User> usersSet = new HashSet<>(); // the participants
        for(Long uID : userIDS) {
            User u = users.findOne(uID);
            if(u == null)
                throw new Exception("Not all the users exist!\n");
            usersSet.add(u);
        }
        messages.findAll().forEach(msg -> {
            if(msg.getParticipants().equals(usersSet))
                mess.add(msg);
        });
        mess.sort(Comparator.comparing(Message::getDate));
        if(mess.size() == 0)
            throw new Exception("There are no messages in that chat!\n");
        return mess;
    }

    public void createGroup(String groupName, Long... userIDS) throws UserNotFoundException {
        for(Long u: userIDS)
            if(users.findOne(u) == null)
                throw new UserNotFoundException(String.format("User with the ID #%s doesn't exist!\n", u));

    }

    /**
     * find a message with a specific id
     * @param msgID message's id
     * @return message
     * @throws Exception
     */
    public Message findMessage(Long msgID) throws Exception {
        Message msg = messages.findOne(msgID);
        if(msg == null)
            throw new Exception("Message not found!\n");
        return msg;
    }

    /**
     * Send a message
     * @param fromID from
     * @param content content
     * @param messageReplied reply
     * @param toIDS to
     * @throws Exception
     */
    public void sendMessage(Long fromID, String content, Long messageReplied, Long...  toIDS) throws Exception {
        User from = users.findOne(fromID);
        if(from == null)
            throw new Exception("The sender is non existent with that ID!\n");
        // it the message is normal
        if(messageReplied == null){
            Set<User> to = new HashSet<>();
            for(Long id : toIDS){
                User usr = users.findOne(id);
                if(usr != null)
                    to.add(usr);
            }
            if(to.size() != toIDS.length)
                throw new Exception("Not all the users you chose to send the message exist!\n");
            Message msg = new Message(from, to, content);
            if(messages.save(msg) != null)
                throw new Exception("The message wasn't sent!\n");
            notifyObservers(new MessageObservableEvent(msg));
            return;
        }
        // if the message is a reply
        Message reply = messages.findOne(messageReplied); // the message replied
        Set<User> to = reply.getParticipants();
        if(!to.remove(from))
            throw new Exception("Oh boy... Message reply problems :' )");
        Message msg = new Message(from, to, content);
        msg.setReply(reply);
        if(messages.save(msg) != null)
            throw new Exception("The message wasn't sent!\n");
        notifyObservers(new MessageObservableEvent(msg));
    }

    public void sendGroupMessage(Long fromID, String content, Long messageReplied, Long groupID) throws Exception {
        User from = users.findOne(fromID);
        if(from == null)
            throw new Exception("The sender is non existent with that ID!\n");
        // it the message is normal
        if(groups.findOne(groupID) == null)
            throw new Exception("This group doesn't exist!");
        Message msg = new Message(from, groupID, content, null);
        if(messageReplied == null){
            if(messages.save(msg) != null)
                throw new Exception("The message wasn't sent!\n");
            notifyObservers(new MessageObservableEvent(msg));
            return;
        }
        // if the message is a reply
        Message reply = messages.findOne(messageReplied); // the message replied
        msg.setReply(reply);
        if(messages.save(msg) != null)
            throw new Exception("The message wasn't sent!\n");
        notifyObservers(new MessageObservableEvent(msg));
    }

    public Iterable<Message> getAllMessages(){
        return messages.findAll();
    }

    public Iterable<Message> getUserMessages(Long id) {

        List<Long> belongingGroups = StreamSupport.stream(groups.findAll().spliterator(), false)
                                                    .filter(gr -> {return gr.getUsers().contains(id);})
                                                    .map(Entity::getId)
                                                    .collect(Collectors.toList());
        Predicate<Message> filterMessage = msg -> {
          if(msg.getFrom().getId().equals(id))
              return true;
          if(msg.getTo().contains(id))
            return true;
          return belongingGroups.contains(msg.getGroupID());
        };
        return StreamSupport.stream(messages.findAll().spliterator(), false).filter(filterMessage).collect(Collectors.toList());
    }

    public Iterable<Message> getGroupMessages(Long id){
        return StreamSupport.stream(messages.findAll().spliterator(), false)
                .filter(msg -> {return msg.getGroupID().equals(id);}).collect(Collectors.toList());
    }
}
