package com.socialnetwork.AppGUI.Controllers;

import com.socialnetwork.AppGUI.DTO.UserDTO;
import com.socialnetwork.AppGUI.utils.EventBox;
import com.socialnetwork.domain.Event;
import com.socialnetwork.repository.paging.Page;
import com.socialnetwork.repository.paging.Pageable;
import com.socialnetwork.repository.paging.PageableImplementation;
import com.socialnetwork.service.*;
import com.socialnetwork.tools.events.EventObserver;
import com.socialnetwork.tools.events.ObservableEvent;
import com.socialnetwork.tools.observer.Observer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EventController implements Observer<ObservableEvent> {
    @FXML
    public Button createEventButton;
    @FXML
    public Button goFrontButton;
    @FXML
    public Button goBackButton;
    @FXML
    public Button goingButton;
    @FXML
    public BorderPane displayPane;

    private int page = 0;
    private int size = 1;

    private Pageable pageable;

    private UserDTO userDTO;
    private UserService userService;
    private EventService eventService;
    private Stage stage;

    public void setServices(UserDTO userDTO, UserService userService, EventService eventService) {
        this.userService = userService;
        this.eventService = eventService;
        this.userDTO = userDTO;
    }

    public void load(){
        loadStyle();
        loadEvent();
        loadInterface();
        assignSignals();
    }

    private void loadStyle() {
        displayPane.setStyle("-fx-border-radius: 10px;" +
                "-fx-background-radius: 10px;");

    }

    private void loadInterface() {
        if(((EventBox)displayPane.getCenter()).getEvent().getParticipants().contains(userDTO.getID())){
            goingButton.setText("X  Going");
        } else
            goingButton.setText("GO");
    }

    private void assignSignals() {
        goBackButton.setOnAction(event -> {
            if(this.page > 0) {
                this.page--;
                loadEvent();
            }
        });
        goFrontButton.setOnAction(event ->{
            if(this.page + 1 < eventService.getNoOfEvents()) {
                this.page++;
                loadEvent();
            }
        });
        createEventButton.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/socialnetworkgui/create_event_window.fxml"));
            Pane root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            CreateEventController controller = loader.getController();
            controller.setServices(eventService);
            controller.load();
            Scene scene = new Scene(root);
            Stage temp = new Stage();
            temp.setScene(scene);
            temp.show();
        });

        goingButton.setOnAction(event -> {
            Event ev = ((EventBox) displayPane.getCenter()).getEvent();
           if(((EventBox) displayPane.getCenter()).getEvent().going(userDTO.getID())){
               // remove participation
               eventService.removeParticipant(ev.getId(), userDTO.getID());
               goingButton.setText("GO");
           }
           else {
                eventService.addParticipant(ev.getId(), userDTO.getID());
                goingButton.setText("X   Going");
           }
            loadEvent();
        });
    }

    private void loadEvent() {
        Pageable pageable = new PageableImplementation(this.page, 1);
        List<Event> events = eventService.findAll(pageable).getContent().collect(Collectors.toList());
        if(events.size() > 0) {
            goFrontButton.setVisible(true);
            goBackButton.setVisible(true);
            goingButton.setVisible(true);
            Event event = events.get(0);
            if(event.going(userDTO.getID()))
                goingButton.setText("X  Going");
            else goingButton.setText("GO");

            displayPane.setCenter(new EventBox(event));
        }
        else {
            if(this.page == 0) {

                ///background_wif_event



                displayPane.setCenter(new Label("there are no events"));
                goFrontButton.setVisible(false);
                goBackButton.setVisible(false);
                goingButton.setVisible(false);
            }
        }
    }


    @Override
    public void update(ObservableEvent observableEvent) {
        if(Objects.equals(observableEvent.getType(), "event")) {
            if (!goFrontButton.isVisible())
                loadEvent();
        }
    }
}
