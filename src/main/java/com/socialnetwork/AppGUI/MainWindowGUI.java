package com.socialnetwork.AppGUI;

import com.socialnetwork.AppGUI.Controllers.MainWindowController;
import com.socialnetwork.domain.FriendRequest;
import com.socialnetwork.domain.User;
import com.socialnetwork.models.FriendrequestModel;
import com.socialnetwork.models.UserModel;
import com.socialnetwork.service.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.LogManager;
import java.util.stream.StreamSupport;

public class MainWindowGUI extends Stage {
    private final GroupService groupService;
    private final EventService eventService;
    private final SecurityService securityService;
    private UserService userService;
    private User authenticatedUser;
    private ServiceFriendship friendshipService;
    private MessageService messageService;
    Predicate<User> withoutAuthenticatedUser;
    Predicate<FriendRequest> usersFriendrequests;
    
    private void loadGUI() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetworkgui/main_window.fxml"));
        Pane root = loader.load();
        MainWindowController controller = loader.getController();
        controller.setServices(authenticatedUser, userService, friendshipService, messageService, groupService, eventService, securityService);
        controller.load();
        Scene scene = new Scene(root);
        this.setScene(scene);
        this.setTitle("YOLO");
        this.show();
    }

    public MainWindowGUI(User au, UserService us, ServiceFriendship fs, MessageService messageService, GroupService groupService, EventService eventService, SecurityService securityService) throws Exception {
        this.authenticatedUser = au;
        this.userService = us;
        this.friendshipService = fs;
        this.messageService = messageService;
        this.groupService = groupService;
        this.eventService = eventService;
        this.securityService = securityService;
        withoutAuthenticatedUser = u -> {return !u.getUserName().equals(authenticatedUser.getUserName());};
        usersFriendrequests = u -> {return u.getIdTo().equals(authenticatedUser.getId()) || u.getIdFrom().equals(authenticatedUser.getId());};
        loadGUI();
    }

}
