package com.socialnetwork.AppGUI.utils;

import com.socialnetwork.domain.User;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class UserBox extends HBox {
    private User u;

    public UserBox(User u){
        this.u = u;
        this.getChildren().add(new Label(u.getUserName()));
    }

    public User getUser(){
        return this.u;
    }
}
