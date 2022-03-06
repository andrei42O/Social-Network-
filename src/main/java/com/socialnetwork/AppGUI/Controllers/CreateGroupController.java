package com.socialnetwork.AppGUI.Controllers;

import com.socialnetwork.AppGUI.DTO.UserDTO;
import com.socialnetwork.AppGUI.GUI;
import com.socialnetwork.domain.Entity;
import com.socialnetwork.domain.User;
import com.socialnetwork.service.GroupService;
import com.socialnetwork.service.MessageService;
import com.socialnetwork.service.ServiceFriendship;
import com.socialnetwork.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CreateGroupController {
    @FXML
    public ComboBox<User> userListComboBox;
    @FXML
    public Button addButton;
    @FXML
    public TextField groupNameInput;
    @FXML
    public AnchorPane paneID;
    private ObservableList<User> userListData = FXCollections.observableArrayList();
    @FXML
    public ListView<User> selectedUsers;
    private ObservableList<User> selectedUsersData = FXCollections.observableArrayList();
    @FXML
    public Button createButton;


    private UserDTO userDTO;
    private UserService userService;
    private ServiceFriendship friendshipService;
    private MessageService messageService;
    private GroupService groupService;
    private Stage stage;

    public void setServices(UserDTO userDTO, UserService userService, ServiceFriendship friendshipService, MessageService messageService, GroupService groupService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.groupService = groupService;
        this.userDTO = userDTO;
    }

    public void load() {
        loadPhoto();
        loadUsers();
        loadSelectedUsers();
        assignSignals();
    }

    private void loadPhoto() {
    }

    private void loadSelectedUsers() {
        selectedUsersData.clear();
        selectedUsers.setItems(selectedUsersData);
        selectedUsers.setCellFactory(param -> {
            ListCell<User> cell = new ListCell<User>() {
                Label username = new Label();
                Button del = new Button("x");
                GridPane box = new GridPane();

                {
                    box.setStyle("-fx-background-color: #e5e5e5");
                    box.add(username, 0, 0);
                    box.add(del, 1, 0);
                    GridPane.setHalignment(username, HPos.LEFT);
                    GridPane.setHalignment(del, HPos.RIGHT);
                    box.setHgap(30);
                    box.setPadding(new Insets(5));
                    del.setOnAction(event -> {
                        User u = param.getFocusModel().getFocusedItem();
                        selectedUsersData.remove(u);
                        userListData.add(u);
                        System.out.println("sal");
                    });
                }

                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        username.setText(item.getUserName());
                        setGraphic(box);
                    }
                }
            };
            return cell;
        });
    }

    private void loadUsers() {
        userListData.clear();
        userService.findAll().forEach(user -> {
            if (!Objects.equals(user.getId(), userDTO.getID())) {
                userListData.add(user);
            }
        });
        userListComboBox.setItems(userListData);
        userListComboBox.setCellFactory(param -> {
                    ListCell<User> cell = new ListCell<User>() {
                        Label username = new Label();
                        HBox box = new HBox(username);
                        {
                            box.setAlignment(Pos.CENTER_LEFT);
                            box.setStyle("-fx-background-color: #e5e5e5");
                        }

                        @Override
                        protected void updateItem(User item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                username.setText(item.getUserName());
                                setGraphic(box);
                            }
                        }
                    };
                    return cell;
                }
        );
    }

    private void assignSignals() {
        addButton.setOnAction(event -> {
            if(userListComboBox.getSelectionModel().isEmpty())
                return;
            User u = userListComboBox.getSelectionModel().getSelectedItem();
            System.out.println(u);
            userListData.remove(u);
            addSelectedUser(u);
        });

        createButton.setOnAction(event -> {
            String groupName = "New Group";
            if(groupNameInput.getText().length() > 0)
                groupName = groupNameInput.getText();
            List<Long> usersIDS = selectedUsersData.stream().map(Entity::getId).collect(Collectors.toList());
            usersIDS.add(userDTO.getID());
            if (usersIDS.size() > 1) {
                try {
                    groupService.createGroup(groupName, usersIDS);
                    GUI.closeWindow(event);
                } catch (Exception e) {
                    GUI.alertUser("The group couldn't be created!\nReasons:\n" + e.getMessage());
                }
            }
        });
    }

    private void addSelectedUser(User u) {
        selectedUsersData.add(u);
    }
}


