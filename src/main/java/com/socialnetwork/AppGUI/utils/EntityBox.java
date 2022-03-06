package com.socialnetwork.AppGUI.utils;

import com.socialnetwork.domain.Group;
import com.socialnetwork.domain.User;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class EntityBox extends BorderPane {
    private User user = null;
    private Group group = null;
    public EntityBox(User u){
        var l1 = new Label(u.getFirstName() + " " + u.getLastName());
        var l2 = new Label( "@" + u.getUserName());
        this.user = u;
        VBox temp = new VBox(l1, l2);
        temp.setAlignment(Pos.CENTER);
        this.setCenter(temp);
        addStyle();
    }
    
    public EntityBox(Group g){
        this.group = g;
        Label l1 = new Label("Group:");
        Label l2 =  new Label(g.getGroupName());
        VBox temp = new VBox(l1, l2);
        temp.setAlignment(Pos.CENTER);
        this.setCenter(temp);
        addStyle();
    }

    private void addStyle() {
        this.setStyle("-fx-background-color: #a5faf7; " +
                "-fx-border-radius: 10px; " +
                "-fx-background-radius:  10px;");
    }

    public void setSize(double fixedWidth, double fixedHeight){
        this.setMaxSize(fixedWidth, fixedHeight);
        this.setMinSize(fixedWidth, fixedHeight);
    }

    public EntityBox(String name, String... participants){
        if(participants.length == 0)
            this.getChildren().addAll(new Label("name"));
        else
            this.getChildren().addAll(new Label("name"), new Label("group"));
        addStyle();

    }
    public EntityBox(){
        super(new Label("#Unknown"));
        addStyle();
    }

    public String getType() {
        if(user == null && group != null)
            return "group";
        if(user != null && group == null)
            return "user";
        return "naspa";
    }

    public Object getEntity() {
        if(user == null && group != null)
            return group;
        if(user != null && group == null)
            return user;
        return null;
    }
}
