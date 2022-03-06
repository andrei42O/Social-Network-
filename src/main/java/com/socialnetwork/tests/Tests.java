package com.socialnetwork.tests;

import com.socialnetwork.domain.*;
import com.socialnetwork.domain.validators.UserValidator;
import com.socialnetwork.domain.validators.ValidationException;
import com.socialnetwork.repository.paging.PagingRepository;

import java.time.LocalDate;
import java.util.*;

public class Tests {

    public static void run(){

        testDomain();
        testValidator();
        //testRepo();
        //testService();
    }

    private static void testDomain(){
        User u1 = new User("Pop", "Mihai");
        u1.setId(5L);

        assert u1.getFirstName().equals("Pop");
        assert u1.getLastName().equals("Mihai");
        assert u1.getId().equals(5L);

        u1.setFirstName("Popescu");
        u1.setLastName("Alex");

        assert u1.getFirstName().equals("Popescu");
        assert u1.getLastName().equals("Alex");

        LocalDate date = LocalDate.of(2017, 2, 13);

        Friendship friendship = new Friendship(15L,17L, date);

        assert friendship.getId1().equals(15L);
        assert friendship.getId2().equals(17L);
        assert friendship.getDate().equals(date);

        Message msg = new Message(u1, new HashSet<>(List.of(u1)), "salut");
        msg.setId(1L);
        assert msg.getId() == 1L;
        assert msg.getFrom().equals(u1);
        assert msg.getReply() == null;
        assert Objects.equals(msg.getTo(), new HashSet<>(List.of(u1)));
        assert msg.getMessage().equals("salut");
        User u2 = new User("Marcel", "Marci");
        Message msg2 = new Message(u2, new HashSet<>(List.of(u1)), "ba", msg);
        msg.setMessage("ceaw");
        msg.setFrom(u2);
        msg.setTo(new HashSet<>(List.of(u2)));
        msg.setReply(msg2);
        assert msg.getFrom().equals(u2);
        assert msg.getReply() == msg2;
        assert Objects.equals(msg.getTo(), new HashSet<>(List.of(u2)));
        assert msg.getMessage().equals("ceaw");

        Long ID1 = 1L;
        Long ID2 = 2L;
        FriendRequest fr = new FriendRequest(ID1, ID2,date);
        assert fr.getIdFrom() == 1L;
        assert fr.getIdTo() == 2L;
        assert fr.getState() == 0;
        assert fr.getDate() == date;
        fr.setState(1L);
        assert fr.getState() == 1L;
        fr.setState(2L);
        assert fr.getState() == 2L;

    }

    private static void testValidator(){
        User u1 = new User("Pop", "Mihai");
        UserValidator util = new UserValidator();
        u1.setId(null);

        try{
            util.validate(u1);
            assert false;
        }
        catch (ValidationException e)
        {
            assert e.getMessage().equals("The id cannot be null!\n");
        }

        u1.setId(5L);
        u1.setFirstName("");
        try{
            util.validate(u1);
            assert false;
        }
        catch (ValidationException e)
        {
            assert e.getMessage().equals("The first name cannot be empty!\nThe first name must start with capital letter and only have letters!\n");
        }

        u1.setFirstName("Alex");
        u1.setLastName("");
        try{
            util.validate(u1);
            assert false;
        }
        catch (ValidationException e)
        {
            assert e.getMessage().equals("The last name cannot be empty!\nThe last name must start with capital letter and only have letters!\n");
        }

    }

    private static void testRepo()  {

    }
/*
    private static void testService(){
        Repository<Long, User> userRepo = new UserFile("Social_Network/src/com/socialnetwork/tests/testfile.txt", new UserValidator());
        Repository<Pair<Long, Long>, Friendship> friendshipRepo = new FriendshipFile("Social_Network/src/com/socialnetwork/tests/friendshiptestfile.txt", new FriendshipValidator());

        List<User> lu = new ArrayList<>();
        List<Friendship> lf = new ArrayList<>();

        UserService userService = new UserService(userRepo, friendshipRepo);
        ServiceFriendship serviceFriendship = new ServiceFriendship(userRepo, friendshipRepo);

        userService.findAll().forEach(lu::add);
        serviceFriendship.findAll().forEach(lf::add);

        assert lu.size() == 0;
        assert lf.size() == 0;

    }
    */

}
