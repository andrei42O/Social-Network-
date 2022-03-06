package com.socialnetwork.AppGUI.Controllers;

import com.socialnetwork.AppGUI.DTO.UserDTO;
import com.socialnetwork.domain.notifications.Notification;
import com.socialnetwork.repository.paging.Pageable;
import com.socialnetwork.service.EventService;
import com.socialnetwork.service.UserService;
import com.socialnetwork.tools.events.EventSubscribtion;
import com.socialnetwork.tools.events.ObservableEvent;
import com.socialnetwork.tools.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.Objects;

public class NotificationsController implements Observer<ObservableEvent> {
    @FXML
    public ListView<Notification> notificationsList;
    public ObservableList<Notification> notificationsListData = FXCollections.observableArrayList();

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
        eventService.registerObserver(this);
        loadNotifications();
    }

    private void loadNotifications() {
        notificationsListData.addAll(eventService.getNotifications(userDTO.getID()));
        notificationsList.setItems(notificationsListData);
        notificationsList.setFixedCellSize(50);
        notificationsList.setCellFactory(param -> {
            ListCell<Notification> cell = new ListCell<Notification>() {
                Label msg = new Label();
                HBox box = new HBox(msg);
                {
                    box.setAlignment(Pos.CENTER_LEFT);
                    box.setStyle("-fx-background-color: #FF4800");
                    box.setMinHeight(30);
                    box.setMaxHeight(30);
                }

                @Override
                protected void updateItem(Notification item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        msg.setText(item.getNotification());
                        setGraphic(box);
                    }
                }
            };
            return cell;
        });
    }

    @Override
    public void update(ObservableEvent observableEvent) {
        if(Objects.equals(observableEvent.getType(), "notification")) {
            notificationsListData.add(((EventSubscribtion)observableEvent).getNotification());
        }
    }
}
