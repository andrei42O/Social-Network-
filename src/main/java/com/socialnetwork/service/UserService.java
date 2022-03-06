package com.socialnetwork.service;

import com.socialnetwork.domain.Friendship;
import com.socialnetwork.domain.Pair;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.validators.ValidationException;
import com.socialnetwork.repository.db.UserDbRepository;
import com.socialnetwork.repository.paging.PagingRepository;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.max;

public class UserService{
    private UserDbRepository users;
    private PagingRepository<Pair<Long, Long>, Friendship> relations;

    /**
     * Constructir
     * @param users repo useri
     * @param relations repo relatii
     */
    public UserService(UserDbRepository users, PagingRepository<Pair<Long, Long>, Friendship> relations) {
        this.users = users;
        this.relations = relations;
    }

    /**
     * Stergere Utilizator
     * @param rawID
     */
    public void delete(Long rawID){
        User user = users.delete(rawID);
        if(user == null)
            throw new ValidationException("No user with the given id.");
        ArrayList<Pair<Long, Long>> toRemove = new ArrayList<Pair<Long, Long>>();
        relations.findAll().forEach(r -> {
            if(Objects.equals(r.getId1(), rawID) || Objects.equals(r.getId2(), rawID))
                toRemove.add(r.getId());
        });
        toRemove.forEach(r -> relations.delete(r));
    }

    /**
     * Update utilizator
     * @param u
     */
    public void update(User u)
    {
        User check = users.update(u);
        if(check != null)
            throw new ValidationException("Eroare la actualizare!");
    }
    /**
     *
     * @return Iterator pe lista de utilizator
     */
    public Iterable<User> findAll(){
        return users.findAll();
    }
    /**
     *
     * @param id id
     * @return utilizator identificat dupa id
     */
    public User findOne(Long id){return users.findOne(id);}

    public User findOne(String username){
        Predicate<User> byUserName = u -> {return u.getUserName().equals(username);};
        List<User> temp = StreamSupport.stream(users.findAll().spliterator(), false).filter(byUserName).collect(Collectors.toList());
        if(temp.size() == 1)
            return temp.get(0);
        return null;
    }
}
