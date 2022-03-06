package com.socialnetwork.models;

import com.socialnetwork.domain.User;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class UserModel {
    private final SimpleStringProperty firstName;
    private final SimpleStringProperty lastName;
    private final SimpleStringProperty userName;
    private final SimpleStringProperty hiddenID;

    public UserModel(String firstName, String lastName, String userName, String hiddenID) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.userName = new SimpleStringProperty(userName);
        this.hiddenID = new SimpleStringProperty(hiddenID);
    }

    public UserModel(User user){
        this.firstName = new SimpleStringProperty(user.getFirstName());
        this.lastName = new SimpleStringProperty(user.getLastName());
        this.userName = new SimpleStringProperty(user.getUserName());
        this.hiddenID = new SimpleStringProperty(user.getId().toString());
    }

    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String o) { firstName.set(o); }

    public String getLastName() { return lastName.get(); }
    public void setLastName(String o) { lastName.set(o); }

    public String getUserName() { return userName.get(); }
    public void setUserName(String o) { userName.set(o); }

    public String getHiddenID() { return hiddenID.get(); }
    public void setHiddenID(String o) { hiddenID.set(o); }

    @Override
    public String toString() {
        return String.format("username: %s\n%s", getUserName(), getFirstName() + " " + getLastName());
    }
}
