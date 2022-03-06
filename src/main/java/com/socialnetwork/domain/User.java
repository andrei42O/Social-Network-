package com.socialnetwork.domain;

import com.socialnetwork.domain.validators.FriendException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User extends Entity<Long> {
    private String firstName;
    private String lastName;
    private List<Long> friendsID;
    private String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<Long> getFriendsID() {
        return friendsID;
    }

    public void setFriendsID(List<Long> friendsID) {
        this.friendsID = friendsID;
    }

    public User(String firstName, String lastName, String userName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
    }

    /**
     * Cosntructor
     * @param firstName stringul de first nae
     * @param lastName stringul de last name
     */
    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = firstName + LocalDateTime.now();
    }

    public User(String firstName, String lastName, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
    }

    /**
     *  Gettere si settere pentru atributele clasei
     */
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Suprascrierea functiei toString
     */
    @Override
    public String toString() {
        return  "ID: " + getId() + " " + firstName + " " + lastName;// + " -> Friends: " + friendsID;
    }


    /**
     * Suprascrierea functiilor equals si hashCode
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName());
    }

}