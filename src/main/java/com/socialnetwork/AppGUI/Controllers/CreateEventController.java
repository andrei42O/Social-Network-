package com.socialnetwork.AppGUI.Controllers;

import com.socialnetwork.AppGUI.GUI;
import com.socialnetwork.AppGUI.utils.EventType;
import com.socialnetwork.service.EventService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.time.LocalDate;

public class CreateEventController {
    @FXML
    public TextArea descriptionArea;
    @FXML
    public ComboBox<String> eventTypeComboBox;
    public ObservableList<String> eventTypeData = FXCollections.observableArrayList();
    @FXML
    public TextField eventNameLabel;
    @FXML
    public Button createButton;
    public DatePicker datePicker;
    public AnchorPane paneID;
    @FXML
    private EventService eventService;

    public void setServices(EventService eventService) {
        this.eventService = eventService;
    }

    public void load(){
        this.paneID.setStyle("-fx-background-image: url('/com/example/socialnetworkgui/images/create_event.jpg')");
        loadEventTypes();
        assignSignals();
    }

    private void loadEventTypes() {
        eventTypeData.clear();
        eventTypeData.addAll("Party", "Meeting", "Concert", "Trip", "Marathon", "Others");
        eventTypeComboBox.setItems(eventTypeData);
    }

    private void assignSignals() {
        createButton.setOnAction(event -> {
            String err = "";
            String eventName = eventNameLabel.getText();
            if(eventName.length() <= 0)
                err += "Give a title to the event!\n";
            String description = descriptionArea.getText();
            LocalDate date = datePicker.getValue();
            if(date == null)
                err += "Please choose a date for the event!\n";
            EventType eventType = Enum.valueOf(EventType.class, eventTypeComboBox.getSelectionModel().getSelectedItem());
            if(eventType == null)
                err += "Please choose an event type!\n";
            if(err.length() > 0){
                GUI.alertUser(err);
                return;
            }
            if(eventService.createEvent(eventName, eventType, date, description) != null) {
                GUI.closeWindow(event);
                return;
            }
            GUI.alertUser("Something went wrong when creating this group...");
        });
    }
}
