package com.socialnetwork.AppGUI.Controllers;

import com.socialnetwork.AppGUI.DTO.UserDTO;
import com.socialnetwork.domain.User;
import com.socialnetwork.models.UserModel;
import com.socialnetwork.service.GroupService;
import com.socialnetwork.service.MessageService;
import com.socialnetwork.service.ServiceFriendship;
import com.socialnetwork.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class UserDetailsController {
    private UserDTO userDTO;
    private User authenticatedUser;
    private UserService userService;
    private ServiceFriendship friendshipService;
    private MessageService messageService;
    private GroupService groupService;

    @FXML
    private Label usernameLabel;
    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private ListView<User> friendsList;
    @FXML
    private TitledPane friendsListTitledPane;

    ObservableList<User>  friendsObservableList = FXCollections.observableArrayList();

    public void setServices(User authenticatedUser, UserService userService, ServiceFriendship friendshipService, MessageService messageService, GroupService groupService){
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.groupService = groupService;
        this.userDTO = new UserDTO(authenticatedUser, friendshipService, userService, messageService, groupService);
    }


    public void loadData() {
        loadLabels();
        loadFriendList();
    }

    private void loadFriendList() {
        friendsObservableList.clear();
        friendsObservableList.addAll(userDTO.getFriends());
        friendsList.setFixedCellSize(60);
        friendsList.setItems(friendsObservableList);
        friendsList.setCellFactory(param -> {
            ListCell<User> cell = new ListCell<User>() {
                Label username = new Label();
                Label fullName = new Label();
                GridPane box = new GridPane();
                {
                    box.add(fullName, 0, 0, 2, 1);
                    box.add(username, 0, 1, 2, 1);
                    box.setAlignment(Pos.CENTER);
                    GridPane.setHalignment(fullName, HPos.CENTER);
                    GridPane.setHalignment(username, HPos.CENTER);
                    box.setStyle("-fx-background-image: url('/com/example/socialnetworkgui/images/background_pastel.jpg');" +
                            "-fx-background-radius: 10px;" +
                            "-fx-background-insets: 5;");
                    username.setStyle("    -fx-font-smoothing-type: lcd;\n" +
                            "    -fx-fill: black;\n" +
                            "    -fx-font-size: 9pt;" +
                            "-fx-font-weight: bold;");
                    fullName.setStyle("    -fx-font-smoothing-type: lcd;\n" +
                            "    -fx-fill: black;\n" +
                            "    -fx-font-size: 9pt;" +
                            "-fx-font-weight: bold;");
                }

                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        username.setText("@" + item.getUserName());
                        fullName.setText(item.getFirstName() + " " + item.getLastName());
                        setGraphic(box);
                    }
                }

            };
            return cell;
        });
    }

    private HBox createUserHBox(User user) {
        HBox details = new HBox(new Label(user.getFirstName()), new Label(user.getUserName()));
        details.setAlignment(Pos.CENTER);
        details.setSpacing(10);
        details.setMaxHeight(40);
        details.setMinHeight(40);
        details.setStyle("-fx-background-color: #FF4700; " +
                "-fx-border-radius: 10px; " +
                "-fx-background-radius:  10px");
        return details;
    }

    private void updateFriendList() {

    }

    private void loadLabels() {
        usernameLabel.setText(userDTO.getUserName());
        lastNameLabel.setText(userDTO.getLastName());
        firstNameLabel.setText(userDTO.getFirstName());
    }
}
