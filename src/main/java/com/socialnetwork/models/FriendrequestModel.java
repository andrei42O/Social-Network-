package com.socialnetwork.models;

import javafx.beans.property.SimpleStringProperty;

public class FriendrequestModel {
    private final SimpleStringProperty from;
    private final SimpleStringProperty to;
    private final SimpleStringProperty date;
    private final SimpleStringProperty state;
    private final SimpleStringProperty hiddenID;

    public FriendrequestModel(String from, String to, String date, String state, String hiddenID) {
        this.from = new SimpleStringProperty(from);
        this.to =  new SimpleStringProperty(to);
        this.date =  new SimpleStringProperty(date);
        this.state = new SimpleStringProperty(state);
        this.hiddenID = new SimpleStringProperty(hiddenID);
    }

    public String getFrom(){ return from.get(); }
    public String getTo(){ return to.get(); }
    public String getDate(){return date.get();}
    public String getState(){return state.get();}
    public String getHiddenId(){return hiddenID.get();}
}
