package com.socialnetwork.service;

import com.socialnetwork.domain.FriendRequest;
import com.socialnetwork.domain.Friendship;
import com.socialnetwork.domain.Pair;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.validators.FriendException;
import com.socialnetwork.domain.validators.ValidationException;
import com.socialnetwork.repository.db.UserDbRepository;
import com.socialnetwork.repository.paging.PagingRepository;
import com.socialnetwork.tools.events.FriendRequestEvent;
import com.socialnetwork.tools.events.ObservableEvent;
import com.socialnetwork.tools.observer.ThreadSafeObservable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ServiceFriendship extends ThreadSafeObservable<ObservableEvent> {
    private UserDbRepository users;
    private PagingRepository<Pair<Long, Long>, Friendship> relations;
    private PagingRepository<Long, FriendRequest> friendrequests;

    /**
     * Constructor
     * @param users
     * @param relations
     */
    public ServiceFriendship(UserDbRepository users, PagingRepository<Pair<Long, Long>, Friendship> relations, PagingRepository<Long, FriendRequest> friendrequests)
    {
        this.users = users;
        this.relations = relations;
        this.friendrequests = friendrequests;
    }

    /**
     * Adaugare de prietenie
     * @param idUser1 -> id prim utilizator
     * @param idUser2 -> id al doilea utilizator
     */
    public void save(Long idUser1, Long idUser2)
    {
        User user1 = users.findOne(idUser1);
        String message = "";
        if(user1 == null)
            message += "The user with the first given id does not exist.\n";
        User user2 = users.findOne(idUser2);
        if(user2 == null)
            message += "The user with the second given id does not exist.\n";

        if(message!="")
            throw new ValidationException(message);

        Friendship relation = new Friendship(idUser1, idUser2, LocalDate.now());
        relation.setId(new Pair(idUser1, idUser2));
        Friendship save = relations.save(relation);
        if(save != null)
            throw new ValidationException("A friendship relation is already set between the two given users");
    }

    /**
     * Eliminare prietenie
     * @param idUser1 -> id prim utilizator
     * @param idUser2 -> id al doilea utilizator
     */
    public void delete(Long idUser1, Long idUser2) {
        Pair id = new Pair(idUser1, idUser2);
        Friendship relation = relations.delete(id);
        if(relation == null)
            throw new ValidationException("No relation was found between the tho given users.");
    }

    /**
     *
     * @param idUser1
     * @param idUser2
     * @return Prietenia identificate de perechea de id-uri
     */
    public Friendship findOne(Long idUser1, Long idUser2){
        Pair id = new Pair(idUser1, idUser2);
        return relations.findOne(id);
    }

    /**
     *
     * @return Iterabil cu lista de prietenii
     */
    public Iterable<Friendship> findAll(){
        return relations.findAll();
    }

    /**
     * @param id -> utilizator
     * @return prietenii unui utilizator
     */
    public ArrayList<User> getUserFriends(Long id)
    {
        if(users.findOne(id)==null)
            throw new ValidationException("No user with the given id.");
        ArrayList<User> friends= new ArrayList<>();
        relations.findAll().forEach(r -> {
            if(r.getId1() == id){
                User friend = users.findOne(r.getId2());
                friends.add(friend);
            }
            else if(r.getId2() == id){
                User friend = users.findOne(r.getId1());
                friends.add(friend);
            }
        });
        return friends;
    }

    /**
     * @return numarul de comunitati
     */
    public Integer noCommunities(){
        ArrayList<ArrayList<User>> communities = new ArrayList<>();
        findConnectedComponents(communities);
        return communities.size();
    }

    /**
     * Identifica cea mai sociabila comunitate
     * Cea mai sociabila comunitate este cea cu numarul max de noduri
     * @return cea mai sociabila comunitate
     */
    public ArrayList<User> mostSociableCommunity(){
        ArrayList<ArrayList<User>> comms = new ArrayList<>();
        findConnectedComponents(comms);
        ArrayList<User> maxCom = null;
        int max = 0;
        for (ArrayList<User> comm : comms)
            if (comm.size() > max) {
                max = comm.size();
                maxCom = comm;
            }
        return maxCom;
    }

    /**
     * functie dfs
     * @param user
     * @param visited
     * @param component
     */
    private void dfs(User user, ArrayList<Long> visited, ArrayList<User> component) {
        visited.add(user.getId());
        component.add(user);
        getUserFriends(user.getId()).forEach(friend -> {
            if (visited.indexOf(friend.getId())<0)
                dfs(friend, visited, component);
        });
    }

    /**
     * gaseste componenetele conexe dintr-un graf
     * apelari multiple de dfs
     * @param components lista de componente conexe
     */
    private void findConnectedComponents(ArrayList<ArrayList<User>> components) {
        ArrayList<Long> visited = new ArrayList<>();
        users.findAll().forEach(user -> {
            ArrayList<User> component = new ArrayList<>();
            if (visited.indexOf(user.getId())<0) {
                dfs(user, visited, component);
                components.add(component);
            }
        });
    }

    /**
     * Task 2
     * @param userID user's id
     * @param month month
     * @return iterable with user's friend
     * @throws Exception
     */
    public Iterable<Pair<User, LocalDate>> getFriendsByMonth(Long userID, int month) throws Exception {
        if(month < 1 || month > 12)
            throw new Exception("The month must be between 1 and 12!\n");
        List<Pair<User, LocalDate>> ret = new ArrayList<>();
        User user = users.findOne(userID);
        if(user == null)
            throw new Exception("There is no user with that id!\n");
        Predicate<Long> filterFriendsByMonth = id -> (relations.findOne(new Pair<>(id, userID)).getDate().getMonthValue() == month);
        user.getFriendsID().stream().filter(filterFriendsByMonth).collect(Collectors.toList()).forEach(id -> {
            ret.add(new Pair<>(users.findOne(id), relations.findOne(new Pair<>(userID, id)).getDate()));
        });
        if(ret.size() > 0)
            return ret;
        throw new FriendException("The user has no friendships in that month!\n");
    }

    /**
     * Return friends list of a specific user
     * @param userID user's id
     * @return friends list
     * @throws Exception
     */
    public Iterable<Pair<User, LocalDate>> getFriends(Long userID) throws Exception {

        List<Pair<User, LocalDate>> ret = new ArrayList<>();

        User user = users.findOne(userID);

        if(user == null)
            throw new Exception("There is no user with that id!\n");

        user.getFriendsID().stream().filter(id -> {

            return relations.findOne(new Pair<>(id, userID)).getId1()== userID || relations.findOne(new Pair<>(id, userID)).getId2()== userID;

        }).collect(Collectors.toList()).forEach(id -> {

            ret.add(new Pair<>(users.findOne(id), relations.findOne(new Pair<>(userID, id)).getDate()));

        });

        if(ret.size() > 0)

            return ret;

        throw new FriendException("The user has no friends!\n");

    }

    /**
     *
     * @return all friend requests
     */
    public Iterable<FriendRequest> findAll_friendrequests(){
        return friendrequests.findAll();
    }

    public Iterable<FriendRequest> getUserFriendRequests(Long userID){
        Predicate<FriendRequest> containingUser = fr -> {return fr.getIdFrom().equals(userID) || fr.getIdTo().equals(userID);};
        return StreamSupport.stream(friendrequests.findAll().spliterator(), false)
                .filter(containingUser)
                .collect(Collectors.toList());
    }

    /**
     * save a friend request
     * @param from
     * @param to
     */
    public FriendRequest save_friendrequests(Long from, Long to)
    {
        String message = "";
        if(Objects.equals(from, to))
            throw new ValidationException("There must be 2 different persons for a friend request!");
        User user1 = users.findOne(from);
        if(user1 == null)
            message += "The user with the first given id does not exist.\n";
        User user2 = users.findOne(to);
        if(user2 == null)
            message += "The user with the second given id does not exist.\n";
        if(message!="")
            throw new ValidationException(message);
        List<Long> frnds = user1.getFriendsID();
        for (Long frnd : frnds) if (Objects.equals(frnd, to)) {message += "Users  are already friends!\n"; break;}
        if(message!="")
            throw new ValidationException(message);
        FriendRequest fr = new FriendRequest(from, to, LocalDate.now());
        FriendRequest save = friendrequests.save(fr);
        if(save != null)
            throw new ValidationException("A friendship request is already set between the two given users");
        return fr;
    }

    /**
     * update a friend request
     * @param u new friend request
     */
    public void update_friendrequests(FriendRequest u)
    {
        if(u.getState() == 1L)
            save(u.getIdFrom(), u.getIdTo());
        FriendRequest check = friendrequests.update(u);
        if(check != null)
            throw new ValidationException("Eroare la actualizare!");
        notifyObservers(new FriendRequestEvent(u));
    }

    public void cancel_friendrequest(Long id)
    {
        FriendRequest fr = friendrequests.findOne(id);
        fr.setState(2L);
        if(friendrequests.update(fr) == null){
            notifyObservers(new FriendRequestEvent(fr));
        }
    }

    public FriendRequest deleteFriendrequest(Long id) {
        FriendRequest fr = friendrequests.delete(id);
        if(fr != null){
            notifyObservers(new FriendRequestEvent(fr));
        }
        return fr;
    }

    public void silent_delete(FriendRequest fr) {
        friendrequests.delete(fr.getId());
    }
}
