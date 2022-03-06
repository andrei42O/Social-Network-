package com.socialnetwork.AppGUI;

import com.socialnetwork.AppGUI.Controllers.LoginController;
import com.socialnetwork.AppGUI.Controllers.ReportsController;
import com.socialnetwork.AppGUI.DTO.UserDTO;
import com.socialnetwork.service.GroupService;
import com.socialnetwork.service.MessageService;
import com.socialnetwork.service.ServiceFriendship;
import com.socialnetwork.service.UserService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class ReportsWindowGUI extends Stage {
    private UserService userService;
    private ServiceFriendship friendshipService;
    private MessageService messageService;
    private GroupService groupService;
    private UserDTO userDTO;

    public ReportsWindowGUI(UserService userService, ServiceFriendship friendshipService, MessageService messageService, GroupService groupService, UserDTO userDTO) throws IOException {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.groupService = groupService;
        this.userDTO = userDTO;
        loadGUI();
    }

    private void loadGUI() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetworkgui/reports_window.fxml"));
        Pane root = loader.load();
        ReportsController controller = loader.getController();
        controller.setServices(userDTO, userService, friendshipService, messageService, groupService);
        controller.setStage(this);
        controller.load();
        Scene scene = new Scene(root);
        this.setScene(scene);
        this.setTitle("YOLO");
        this.show();
    }

}