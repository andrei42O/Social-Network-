package com.socialnetwork.service;

import com.socialnetwork.domain.Credentials;
import com.socialnetwork.domain.User;
import com.socialnetwork.repository.CrudRepository;
import com.socialnetwork.repository.db.SecurityRepository;
import com.socialnetwork.repository.db.UserDbRepository;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SecurityService {
    private SecurityRepository credentials;
    private UserDbRepository users;
    private Long id;

    public SecurityService(SecurityRepository credentials, UserDbRepository users) {
        this.credentials = credentials;
        this.users = users;
        this.id = findMax();
    }

    public User registerUser(String firstName, String lastName, String userName, String hashedPassword){
        User user = new User(firstName, lastName, userName);
        user.setId(id++);
        if(users.save(user) == null
                && credentials.save(new Credentials(userName, hashedPassword, user.getId())) == null) {
            return user;
        }
        return null;
    }

    public User login(String userName, String hashedPassword) {
        User u = users.findOne(userName);
        if(u == null)
            return null;
        Long id = credentials.findOne(userName).getId();
        if(Objects.equals(u.getId(), id))
            return u;
        return null;
    }

    public Long findMax(){
        Long i = 0L;
        for(User u: users.findAll()){
            if(u.getId() > i)
                i = u.getId();
        }
        return i + 1;
    }
}
