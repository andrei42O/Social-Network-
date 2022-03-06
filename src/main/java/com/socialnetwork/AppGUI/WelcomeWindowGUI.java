package com.socialnetwork.AppGUI;

import com.socialnetwork.AppGUI.Controllers.LoginController;
import com.socialnetwork.domain.User;
import com.socialnetwork.service.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class WelcomeWindowGUI extends Stage {
    private final EventService eventService;
    private UserService userService;
    private ServiceFriendship friendshipService;
    private MessageService messageService;
    private GroupService groupService;
    private SecurityService security;

    public WelcomeWindowGUI(UserService userService, ServiceFriendship friendshipService, MessageService messageService, GroupService groupService, SecurityService security, EventService eventService) throws IOException {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.groupService = groupService;
        this.security = security;
        this.eventService = eventService;
        loadGUI();
    }

    private void loadGUI() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetworkgui/login_window.fxml"));
        Pane root = loader.load();
        LoginController controller = loader.getController();
        controller.setServices(userService, friendshipService, messageService, groupService, security, eventService);
        controller.setStage(this);
        controller.load();
        Scene scene = new Scene(root);
        this.setScene(scene);
        this.setTitle("YOLO");
        //this.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("color_wave.gif")).toExternalForm()));
        this.show();
    }

}
