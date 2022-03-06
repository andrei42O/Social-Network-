package com.socialnetwork.AppGUI.Controllers;

import com.socialnetwork.AppGUI.DTO.UserDTO;
import com.socialnetwork.AppGUI.GUI;
import com.socialnetwork.domain.FriendRequest;
import com.socialnetwork.domain.User;
import com.socialnetwork.service.MessageService;
import com.socialnetwork.service.ServiceFriendship;
import com.socialnetwork.service.UserService;
import com.socialnetwork.tools.events.FriendRequestEvent;
import com.socialnetwork.tools.events.ObservableEvent;
import com.socialnetwork.tools.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipController implements Observer<ObservableEvent> {
    public Button sendRequestButton;
    @FXML
    private ComboBox<String> searchComboBox;
    @FXML
    public ListView<FriendRequest> friendshipHistoryList;
    @FXML
    public ListView<FriendRequest> friendRequestList;
    @FXML
    public GridPane gridPane;

    private UserDTO userDTO;
    UserService userService;
    ServiceFriendship friendshipService;
    MessageService messageService;

    private ObservableList<FriendRequest> friendshipListData = FXCollections.observableArrayList();
    private ObservableList<FriendRequest> friendshipHistoryData =  FXCollections.observableArrayList();
    private ObservableList<String> usersData = FXCollections.observableArrayList();

    public void setUserDTO(UserDTO userDTO){
        this.userDTO = userDTO;
    }

    public void setServices(UserService us, ServiceFriendship fs, MessageService messageService){
        this.userService = us;
        this.friendshipService = fs;
        this.messageService = messageService;
    }

    private void adjustStyle(){
        friendRequestList.setStyle("    -fx-background-color: rgba(255, 255, 255, 5);\n" +
                "    -fx-background-insets: 10;");
        friendshipHistoryList.setStyle("-fx-background-color: transparent;" +
                "-fx-border-radius: 10px;" +
                "-fx-border-width: 3px;");
        gridPane.setHgap(10);
        searchComboBox.setMaxHeight(200);
        searchComboBox.setEditable(true);
    }

    public void load(){
        adjustStyle();
        loadFriendshipRequests();
        loadFriendshipHistory();
        loadSearchBar();
        assignSignals();
        friendshipService.registerObserver(this);
    }

    private void assignSignals() {
        sendRequestButton.setOnAction(event -> {
            if(searchComboBox.getSelectionModel().isEmpty())
                return;
            String username = searchComboBox.getSelectionModel().getSelectedItem();
            username = username.substring(1);
            try {
                FriendRequest fr = friendshipService.save_friendrequests(
                        userDTO.getID(),
                        userService.findOne(username).getId()
                );
                if(fr != null) {
                    friendshipListData.add(fr);
                }
                else GUI.alertUser("There is already a friend request between you and @" + username);
            } catch(Exception ignored){
            }
        });
    }

    private void loadSearchBar() {
        usersData.clear();
        List<Long> temp = new ArrayList<>();
        try {
            temp = StreamSupport.stream(friendshipService.getFriends(userDTO.getID()).spliterator(), false).map(pair -> {return pair.getFirst().getId();}).collect(Collectors.toList());;
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Long> finalTemp = temp;
        StreamSupport.stream(userService.findAll().spliterator(), false)
                                                                    .filter(user -> {return !finalTemp.contains(user.getId());})
                                                                    .forEach(user -> {usersData.add("@" + user.getUserName());});
        usersData.remove("@" + userDTO.getUserName());
        searchComboBox.setItems(usersData);
    }

    private void loadFriendshipRequests() {
        friendshipListData.clear();
        friendshipListData.addAll(userDTO.getFriendRequests());
        friendRequestList.setItems(friendshipListData);
        friendRequestList.setCellFactory(param -> {
            ListCell<FriendRequest> cell = new ListCell<FriendRequest>() {
                FriendRequest fr = null;
                Label username = new Label();
                Label msg = new Label();
                Button accept = new Button("ACCEPT");
                Button decline = new Button("DECLINE");
                BorderPane br = new BorderPane();
                {
                    VBox asd = new VBox(username, msg, new Label(), decline, accept);
                    accept.setVisible(true);
                    asd.setAlignment(Pos.CENTER);
                    br.setCenter(asd);
                    username.setStyle("-fx-font-family: Verdana; -fx-text-fill: black;");
                    msg.setStyle("-fx-font-family: Verdana; -fx-text-fill: black;");
                    accept.setStyle("-fx-background-color: green;" +
                            "-fx-text-fill: white;");
                    decline.setStyle("-fx-background-color: red;" +
                            "-fx-text-fill: white;");
                    accept.setOnAction(event -> {
                        fr.setState(1L);
                        friendshipService.update_friendrequests(fr);
                        friendshipListData.remove(fr);
                    });
                    decline.setOnAction(event -> {
                        System.out.println("ALO GARA");
                        if (!accept.isVisible()) {
                            friendshipService.silent_delete(fr);
                        }
                        else {
                            friendshipService.cancel_friendrequest(fr.getId()); // 2 means canceled? idfk
                        }
                        friendshipListData.remove(fr);
                    });
                }

                @Override
                protected void updateItem(FriendRequest item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        fr = item;
                        if(Objects.equals(item.getIdFrom(), userDTO.getID())){
                            accept.setVisible(false);
                            username.setText("Cerere trimisa catre");
                            msg.setText("@" + userService.findOne(item.getIdTo()).getUserName());
                            decline.setText("CANCEL");
                        }
                        else {
                            username.setText("@" + userService.findOne(item.getIdFrom()).getUserName());
                            msg.setText("doreste sa fie prietenul tau");
                        }
                        setGraphic(br);
                    }
                }
            };
            return cell;
        });
    }

    private void loadFriendshipHistory(){
        friendshipHistoryData.clear();
        friendshipHistoryData.addAll(userDTO.getFriendRequestsHistory());
        friendshipHistoryList.setItems(friendshipHistoryData);
        friendshipHistoryList.setCellFactory(param -> {
            ListCell<FriendRequest> cell = new ListCell<FriendRequest>() {
                Label msg = new Label();
                Label date = new Label();
                VBox box = new VBox(msg, date);
                {
                    box.setMaxWidth(friendshipHistoryList.getMinWidth());
                    box.setMinWidth(friendshipHistoryList.getMinWidth());
                    msg.setWrapText(true);
                    box.setAlignment(Pos.CENTER);
                    box.setStyle("-fx-background-image: url('/com/example/socialnetworkgui/images/login_gif.gif')");
                }

                @Override
                protected void updateItem(FriendRequest item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        String text = "";
                        if(item.getState() == 2){
                            if(Objects.equals(item.getIdFrom(), userDTO.getID()))
                                text = String.format("@%s ti-a refuzat cererea de prietenie", userService.findOne(item.getIdTo()).getUserName());
                            else if(Objects.equals(item.getIdTo(), userDTO.getID()))
                                text = String.format("Ai refuzat cererea de prietenie de la @%s", userService.findOne(item.getIdFrom()).getUserName());
                        } else if( item.getState() == 1) {
                            if (Objects.equals(item.getIdFrom(), userDTO.getID()))
                                text = String.format("Acum esti prieten cu @%s", userService.findOne(item.getIdTo()).getUserName());
                            else if (Objects.equals(item.getIdTo(), userDTO.getID()))
                                text = String.format("Tu si @%s sunteti acum prieteni", userService.findOne(item.getIdFrom()).getUserName());
                        }
                        msg.setText(text);
                        date.setText(item.getDate().toString());
                        setGraphic(box);
                    }
                }
            };
            return cell;
        });
    }


    @Override
    public void update(ObservableEvent observableEvent) {
        if(Objects.equals(observableEvent.getType(), "friendrequest")){
            FriendRequest fr = ((FriendRequestEvent)observableEvent).getFr();
            if(fr.getState() != 0)
                friendshipHistoryData.add(fr);
        }
    }
}
