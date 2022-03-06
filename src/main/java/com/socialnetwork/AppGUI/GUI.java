package com.socialnetwork.AppGUI;

import com.socialnetwork.domain.validators.FriendshipValidator;
import com.socialnetwork.domain.validators.UserValidator;
import com.socialnetwork.repository.db.*;
import com.socialnetwork.service.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class GUI extends Application {

    private UserService userService;
    private ServiceFriendship serviceFriendship;
    private MessageService messageService;
    private GroupService groupService;
    private SecurityService security;
    private EventService eventService;

    public static void closeWindow(ActionEvent event) {
        final Node source = (Node) event.getSource();
        final Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    public static void closeWindow(Stage stage) {
        stage.close();
    }


    private void initialize(){
        FriendRequestDbRepository friendshipRepo = null;
        UserValidator userValidator = new UserValidator();
        FriendshipValidator friendshipValidator = new FriendshipValidator();
        UserDbRepository userRepo = null;
        MessageDbRepository messageDbRepository = null;
        FriendshipDbRepository friendshipDbRepository = null;
        GroupRepository groupRepository = null;
        SecurityRepository securityRepository = null;
        EventRepository eventRepository = null;
        String parola = "admin";
        String username = "postgres";
        try {
            userRepo = new UserDbRepository("jdbc:postgresql://localhost:5432/socialnetwork",username, parola, userValidator);
            messageDbRepository = new MessageDbRepository("jdbc:postgresql://localhost:5432/socialnetwork",username, parola);
            friendshipDbRepository = new FriendshipDbRepository("jdbc:postgresql://localhost:5432/socialnetwork",username, parola, friendshipValidator);
            groupRepository = new GroupRepository("jdbc:postgresql://localhost:5432/socialnetwork",username, parola);
            friendshipRepo = new FriendRequestDbRepository("jdbc:postgresql://localhost:5432/socialnetwork",username, parola);
            securityRepository  = new SecurityRepository("jdbc:postgresql://localhost:5432/socialnetwork", username, parola);
            eventRepository  = new EventRepository("jdbc:postgresql://localhost:5432/socialnetwork", username, parola);
            userService = new UserService(userRepo, friendshipDbRepository);
            serviceFriendship = new ServiceFriendship(userRepo, friendshipDbRepository, friendshipRepo);
            messageService = new MessageService(messageDbRepository, userRepo, groupRepository);
            groupService = new GroupService(groupRepository, userRepo);
            security = new SecurityService(securityRepository, userRepo);
            eventService = new EventService(userRepo, eventRepository);
        } catch (Exception e) {
            GUI.alertUser(e.getMessage());
            throw new RuntimeException("Something went wrong");
        }
    }

    public GUI(){
       initialize();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        new WelcomeWindowGUI(userService, serviceFriendship, messageService, groupService, security, eventService);
    }

    public static void alertUser(String message){
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setAlertType(Alert.AlertType.ERROR);
        a.setContentText(message);
        a.show();
    }

    public void run(String[] args){
        initialize();
        launch();
    }
}
