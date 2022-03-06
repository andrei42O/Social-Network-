package com.socialnetwork.AppGUI.Controllers;

import com.socialnetwork.AppGUI.DTO.UserDTO;
import com.socialnetwork.AppGUI.GUI;
import com.socialnetwork.AppGUI.MessagesWindowGUI;
import com.socialnetwork.AppGUI.ReportsWindowGUI;
import com.socialnetwork.AppGUI.WelcomeWindowGUI;
import com.socialnetwork.domain.User;
import com.socialnetwork.service.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class MainWindowController {
    @FXML
    public ToggleButton logoutButton;
    @FXML
    public Label fullNameLabel;
    @FXML
    public Label shownUsernameLabel;
    @FXML
    public Button reportsButton;
    @FXML
    public ImageView notificationsIcon;
    @FXML
    private Tab notificationsTab;
    @FXML
    private Tab homeTab;
    @FXML
    private Tab eventsTab;
    @FXML
    private TabPane tabPane;
    @FXML
    private Tab friendsTab;
    @FXML
    private ImageView profileImage;

    private User authenticatedUser;
    private UserDTO userDTO;
    private UserService userService;
    private ServiceFriendship friendshipService;
    private MessageService messageService;
    private GroupService groupService;
    private EventService eventService;
    private SecurityService securityService;
    private Boolean hasNotifications = false;
    private boolean reloadNotificationsImage = true;

    public void setServices(User au, UserService us, ServiceFriendship fs, MessageService messageService, GroupService groupService, EventService eventService, SecurityService securityService){
        this.userService = us;
        this.authenticatedUser = au;
        this.friendshipService = fs;
        this.messageService = messageService;
        this.groupService = groupService;
        this.eventService = eventService;
        this.securityService = securityService;
        this.userDTO = new UserDTO(authenticatedUser, friendshipService, userService, messageService, groupService);
    }

    public void handleMessageButton() throws Exception {
        new MessagesWindowGUI(userService, friendshipService, messageService, groupService, authenticatedUser);
    }

    public void load(){
        setStyleSheets();
        assignSignals();
        loadProfileLabels();
        loadNotifications();
    }

    private void loadNotifications() {
        if(eventService.getNotifications(userDTO.getID()).size() > 0) {
            notificationsIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/example/socialnetworkgui/images/has_notifications.png")).toExternalForm()));
            hasNotifications = true;
            this.reloadNotificationsImage = true;
        }
    }

    private void loadProfileLabels() {
        fullNameLabel.setText(authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName());
        shownUsernameLabel.setText("@" + authenticatedUser.getUserName());
    }

    public void setStyleSheets() {
        profileImage.setStyle("-fx-border-radius: 20; -fx-background-color: #FF4800");
    }

    public void assignSignals() {
        reportsButton.setOnAction(event ->{
            try {
                new ReportsWindowGUI(userService, friendshipService, messageService, groupService, userDTO);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>(){
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                if(newValue.equals(friendsTab))
                    loadFriendRequestInterface();
                else if(newValue.equals(notificationsTab))
                    loadNotificationsInterface();
                else if(newValue.equals(eventsTab))
                    loadEventsInterface();
                else
                    loadHomeTabInterface();
            }
        });
        logoutButton.setOnAction(event -> {
            try {
                new WelcomeWindowGUI(userService, friendshipService, messageService, groupService, securityService, eventService);
                GUI.closeWindow(event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    tabPane.getSelectionModel().select(homeTab);
    loadHomeTabInterface();

    }

    private void loadEventsInterface() {
        if(this.reloadNotificationsImage && !hasNotifications) {
            notificationsIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/example/socialnetworkgui/images/notification_icon.png")).toExternalForm()));
            this.reloadNotificationsImage = false;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetworkgui/event_window.fxml"));
        Pane pane = null;
        try{
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(pane == null)
            System.out.println("AAAAAAAAAAAAaa");
        System.out.println("AAAAAAAAAAAAaa");
        EventController controller = loader.getController();
        controller.setServices(userDTO, userService, eventService);
        controller.load();
        eventsTab.setContent(pane);
    }

    private void loadNotificationsInterface() {
        this.hasNotifications = false;
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetworkgui/notifications_window.fxml"));
        Pane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NotificationsController userDetailsController = loader.getController();
        userDetailsController.setServices(userDTO, userService, eventService);
        userDetailsController.load();
        notificationsTab.setContent(pane);
    }

    private void loadFriendRequestInterface() {
        if(this.reloadNotificationsImage && !hasNotifications) {
            notificationsIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/example/socialnetworkgui/images/notification_icon.png")).toExternalForm()));
            this.reloadNotificationsImage = false;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetworkgui/friends_page.fxml"));
        Pane pane = null;
        try{
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FriendshipController controller = loader.getController();
        controller.setServices(userService, friendshipService, messageService);
        controller.setUserDTO(new UserDTO(authenticatedUser,
                friendshipService,
                userService,
                messageService,
                groupService)
        );
        controller.load();
        friendsTab.setContent(pane);
    }

    private void loadHomeTabInterface() {
        if(this.reloadNotificationsImage && !hasNotifications) {
            notificationsIcon.setImage(new Image(Objects.requireNonNull(getClass().getResource("/com/example/socialnetworkgui/images/notification_icon.png")).toExternalForm()));
            this.reloadNotificationsImage = false;
        }
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetworkgui/user_details.fxml"));
        Pane pane = null;
        try {
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        UserDetailsController userDetailsController = loader.getController();
        //userDetailsController.setServices(authenticatedUser, userService, friendshipService, messageService);
        userDetailsController.setServices(authenticatedUser, userService, friendshipService, messageService, groupService);
        userDetailsController.loadData();
        homeTab.setContent(pane);
    }
}
