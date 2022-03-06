package com.socialnetwork.AppGUI.utils;

import com.socialnetwork.domain.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.geometry.HPos;

public class EventBox extends VBox {
    private Event event;
    public EventBox(Event event){
        this.setMinSize(250, 250);
        this.setMaxSize(250, 250);
        this.event = event;
        Label title = new Label(event.getName());
        title.setStyle("-fx-font-size: 15px;" +
                "-fx-font-family: 'Arial Black'");
        Label date = new Label(event.getDate().toString());
        date.setStyle("-fx-font-style: 13px");
        Label type = new Label("\"" + event.getType().toString() + "\"");
        type.setStyle("-fx-font-family: 'Berlin Sans FB Demi'");
        Label description = new Label(event.getDescription());
        this.getChildren().add(title);
        this.getChildren().add(date);
        this.getChildren().add(type);
        this.getChildren().add(new Label());
        this.getChildren().add(description);
        this.setAlignment(Pos.CENTER);

        this.setStyle("-fx-background-image: url('/com/example/socialnetworkgui/images/background_pastel.jpg');" +
                "-fx-background-size: cover;");
    }

    public Event getEvent(){
        return event;
    }
}
