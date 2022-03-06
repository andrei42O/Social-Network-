package com.socialnetwork.AppGUI.Controllers;

import com.socialnetwork.AppGUI.DTO.UserDTO;
import com.socialnetwork.AppGUI.GUI;
import com.socialnetwork.AppGUI.utils.EntityBox;
import com.socialnetwork.domain.*;
import com.socialnetwork.domain.notifications.Notification;
import com.socialnetwork.service.GroupService;
import com.socialnetwork.service.MessageService;
import com.socialnetwork.service.ServiceFriendship;
import com.socialnetwork.service.UserService;
import com.socialnetwork.tools.events.ObservableEvent;
import com.socialnetwork.tools.events.GroupObservableEvent;
import com.socialnetwork.tools.events.MessageObservableEvent;
import com.socialnetwork.tools.observer.Observer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageController implements Observer<ObservableEvent>{

    @FXML
    public BorderPane aboveMessagesPane;
    private final String aboveMessagesPaneFXML = "                  <BorderPane fx:id=\"aboveMessagesPane\" prefHeight=\"51.0\" prefWidth=\"586.0\" BorderPane.alignment=\"CENTER\">\n" +
            "                     <center>\n" +
            "                        <GridPane BorderPane.alignment=\"CENTER\">\n" +
            "                           <columnConstraints>\n" +
            "                              <ColumnConstraints halignment=\"RIGHT\" hgrow=\"SOMETIMES\" minWidth=\"10.0\" prefWidth=\"100.0\" />\n" +
            "                              <ColumnConstraints halignment=\"RIGHT\" hgrow=\"SOMETIMES\" minWidth=\"10.0\" prefWidth=\"100.0\" />\n" +
            "                              <ColumnConstraints halignment=\"RIGHT\" hgrow=\"SOMETIMES\" minWidth=\"10.0\" prefWidth=\"100.0\" />\n" +
            "                              <ColumnConstraints halignment=\"RIGHT\" hgrow=\"SOMETIMES\" minWidth=\"10.0\" prefWidth=\"100.0\" />\n" +
            "                              <ColumnConstraints halignment=\"CENTER\" hgrow=\"SOMETIMES\" minWidth=\"10.0\" prefWidth=\"100.0\" />\n" +
            "                              <ColumnConstraints halignment=\"LEFT\" hgrow=\"SOMETIMES\" minWidth=\"10.0\" prefWidth=\"100.0\" />\n" +
            "                           </columnConstraints>\n" +
            "                           <rowConstraints>\n" +
            "                              <RowConstraints minHeight=\"10.0\" prefHeight=\"30.0\" vgrow=\"SOMETIMES\" />\n" +
            "                           </rowConstraints>\n" +
            "                           <children>\n" +
            "                              <Label fx:id=\"pageTracker\" text=\"1/n\" GridPane.columnIndex=\"4\" />\n" +
            "                              <Button fx:id=\"goBackButton\" mnemonicParsing=\"false\" text=\"&lt;&lt;&lt;\" GridPane.columnIndex=\"3\" />\n" +
            "                              <Button fx:id=\"goFrontButton\" mnemonicParsing=\"false\" text=\"&gt;&gt;&gt;\" GridPane.columnIndex=\"5\" />\n" +
            "                           </children>\n" +
            "                        </GridPane>\n" +
            "                     </center>\n" +
            "                  </BorderPane>";

    @FXML
    public ListView<EntityBox> chatsList;
    @FXML
    public Label chatName;
    @FXML
    public Label participantsLabel;
    private ObservableList<EntityBox> chatsListData = FXCollections.observableArrayList();
    @FXML
    public ComboBox<User> searchBar;
    private ObservableList<User> searchBarData = FXCollections.observableArrayList();
    @FXML
    public ToggleButton createGroupButton;
    @FXML
    public TextArea textArea;
    @FXML
    public ToggleButton sendButton;
    @FXML
    public ListView<Message> messageList;
    private ObservableList<Message> messageListData = FXCollections.observableArrayList();
    @FXML
    public Label pageTracker;
    @FXML
    public Button goBackButton;
    @FXML
    public Button goFrontButton;
    @FXML
    public ImageView profilePicture;
    @FXML
    public Label fullNameLabel;
    @FXML
    public Label usernameLabel;
    @FXML
    public BorderPane chatTop;
    @FXML
    public ImageView createGroupIcon;
    Pair<Long, String> currentChat = new Pair<Long, String> (-1337L, "");

    private Stage stage;
    private User authenticatedUser;
    private UserService userService;
    private ServiceFriendship friendshipService;
    private MessageService messageService;
    private UserDTO userDTO;
    private GroupService groupService;
    private Set<Long> users;


    public void setServices(UserService userService, ServiceFriendship friendshipService, MessageService messageService, GroupService groupService, User authenticatedUser) {
        this.authenticatedUser = authenticatedUser;
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.groupService = groupService;
        this.userDTO = new UserDTO(authenticatedUser, friendshipService, userService, messageService, groupService);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void load() {
        loadAuthenticatedUserDetails();
        loadChatsList();
        loadSearchBar();
        assignSignals();
        messageService.registerObserver(this);
    }

    private void loadSearchBar() {
        searchBarData.addAll(StreamSupport.stream(userService.findAll().spliterator(), false).filter(user -> {return !Objects.equals(user.getId(), userDTO.getID());}).collect(Collectors.toList()));
        searchBar.setItems(searchBarData);
        searchBar.setEditable(true);
        searchBar.setCellFactory(param -> {
            ListCell<User> cell = new ListCell<User>() {
                Label msg = new Label();
                HBox box = new HBox(msg);
                {
                    box.setAlignment(Pos.CENTER_LEFT);
                }

                @Override
                protected void updateItem(User item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        msg.setText("@" + item.getUserName());
                        setGraphic(box);
                    }
                }
            };
            return cell;
        });
    }

    private void assignSignals() {
        chatsList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EntityBox>() {
            @Override
            public void changed(ObservableValue<? extends EntityBox> observable, EntityBox oldValue, EntityBox newValue) {
                if(oldValue == null){
                    loadMessages(newValue);
                } else
                if (!Objects.equals(newValue.getEntity(), oldValue.getEntity())) {
                    System.out.println("aici");
                    loadMessages(newValue);
                }
            }
        });
        searchBar.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>(){
            @Override
            public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
                if(newValue != null){
                    System.out.println(newValue.getUserName());
                    loadMessages(new EntityBox(newValue));
                }
            }
        });
        textArea.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER){
                String content = textArea.getText();
                sendMessageHandle(content);
            }
        });
        sendButton.setOnAction(event -> {
            String content = textArea.getText();
            sendMessageHandle(content);
        });
        createGroupButton.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/example/socialnetworkgui/create_group_window.fxml"));
            Pane root = null;
            try {
                root = loader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
            groupService.registerObserver(this);
            CreateGroupController controller = loader.getController();
            controller.setServices(userDTO, userService, friendshipService, messageService, groupService);
            controller.load();
            Scene scene = new Scene(root);
            Stage temp = new Stage();
            temp.setScene(scene);
            temp.show();
        });
    }

    private void sendMessageHandle(String content) {
        if(chatsList.getSelectionModel().isEmpty()){
            String userName = chatName.getText();
            userName = userName.substring(1, userName.length());
            User u = userService.findOne(userName);
            sendChatMessage(u, content);
            textArea.clear();
            chatsList.getSelectionModel().select(createChatEntity(u));
            return;
        }
        EntityBox obj = chatsList.getSelectionModel().getSelectedItem();
        if(obj == null){
            return;
        }
        switch (obj.getType()) {
            case "group" -> {
                sendGroupMessage((Group) obj.getEntity(), content);
                textArea.clear();
            }
            case "user" -> {
                sendChatMessage((User) obj.getEntity(), content);
                textArea.clear();
            }
            default -> {}
        }
    }


    private EntityBox createChatEntity(User u) {
        var x = new EntityBox(u);
        if(!chatsListData.contains(x))
            chatsListData.add(x);
        return x;
    }

    private void sendChatMessage(User entity, String content) {
        try {
            messageService.sendMessage(userDTO.getID(), content, null, entity.getId());
        } catch (Exception e) {
            GUI.alertUser("Message couldn't be sent!\nReason:\n" + e.getMessage());
        }
    }

    private void sendGroupMessage(Group entity, String content) {
        Long id = entity.getId();
        try{
            messageService.sendGroupMessage(userDTO.getID(), content, null, id);
        } catch (Exception e) {
            GUI.alertUser("Message couldn't be sent!\nReason:\n" + e.getMessage());
        }
    }

    private void loadMessages(EntityBox item) {
        textArea.clear();
        switch(item.getType()){
            case "group":
                loadGroupMessages((Group)item.getEntity());
                break;
            case "user":
                loadUserMessages((User)item.getEntity());
            default:
                break;
        }
    }

    private void loadUserMessages(User entity) {
        chatName.setText("@" + entity.getUserName());
        participantsLabel.setText("");
        participantsLabel.setVisible(false);
        if(Objects.equals(currentChat.getSecond(), "user") && Objects.equals(currentChat.getFirst(), entity.getId())) {
            // If the chat is already opened
            return;
        }
        currentChat = new Pair<>(entity.getId(), "user");
        messageListData.clear();
        try {
            userDTO.getChat(entity.getId()).forEach(msg -> {
                messageListData.add(msg);
            });
        } catch (Exception e) {
            // e chatul gol
        }
        messageList.setItems(messageListData);
        messageList.setCellFactory(param -> {
            ListCell<Message> cell = new ListCell<Message>(){
                Label lblUserLeft = new Label();
                Label lblTextLeft = new Label();
                Label lblReplyLeft = new Label();
                Label lblReplyRight = new Label();
                Label temp = new Label();
                BorderPane br = new BorderPane();
                VBox vBoxLeft = new VBox(lblReplyLeft, lblUserLeft, lblTextLeft);


                Label lblUserRight = new Label();
                Label lblTextRight = new Label();
                VBox vBoxRight = new VBox(lblReplyRight, lblTextRight, lblUserRight);
                {
                    br.setRight(lblReplyRight);
                    br.setLeft(lblReplyLeft);
                    br.setCenter(temp);

                    vBoxLeft.setAlignment(Pos.CENTER_LEFT);
                    vBoxLeft.setPadding(new Insets(5));
                    vBoxRight.setAlignment(Pos.CENTER_RIGHT);
                    vBoxRight.setPadding(new Insets(5));

                    vBoxLeft.setStyle("-fx-background-image: url('/com/example/socialnetworkgui/images/home_tab_background2.jpg');");
                    vBoxRight.setStyle("-fx-background-image: url('/com/example/socialnetworkgui/images/message_background.jpg');");
                }

                @Override
                protected void updateItem(Message item, boolean empty) {
                    super.updateItem(item, empty);

                    if(empty)
                    {
                        setText(null);
                        setGraphic(null);
                    }
                    else{
                        if(item.getFrom().getId().equals(userDTO.getID()))
                        {
                            //lblUserRight.setText(":" + item.getFrom().getUserName());
                            lblTextRight.setText(item.getMessage());
                            lblReplyLeft.setVisible(false);
                            if(item.getReply() != null){
                                lblReplyRight.setText(String.format("Replied to:\n %s\n%s", item.getReply().getFrom().getUserName(), item.getReply().getMessage()));
                                lblReplyLeft.setVisible(true);
                            }
                            setGraphic(vBoxRight);
                        }
                        else{
                            //lblUserLeft.setText(item.getFrom().getUserName() + ":");
                            lblTextLeft.setText(item.getMessage());
                            lblReplyLeft.setVisible(false);
                            if(item.getReply() != null) {
                                lblReplyLeft.setVisible(true);
                                lblReplyRight.setText(String.format("Replied to:\n %s\n%s", item.getReply().getFrom().getUserName(), item.getReply().getMessage()));
                            }
                            setGraphic(vBoxLeft);
                        }
                    }
                }

            };

            return cell;
        });
    }

    private void loadGroupMessages(Group entity) {
        chatName.setText(entity.getGroupName());
        StringBuilder text = new StringBuilder();
        for (Long u : entity.getUsers()) {
            text.append(userService.findOne(u).getUserName()).append(" ");
        }
        participantsLabel.setVisible(true);
        participantsLabel.setText(text.toString());
        if(Objects.equals(currentChat.getSecond(), "group") && Objects.equals(currentChat.getFirst(), entity.getId())) {
            // If the chat is already opened
            return;
        }
        currentChat = new Pair<>(entity.getId(), "group");
        messageListData.clear();
        try {
            userDTO.getGroupChat(entity.getId()).forEach(msg -> {
                messageListData.add(msg);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        messageList.setItems(messageListData);
        messageList.setCellFactory(param -> {
            ListCell<Message> cell = new ListCell<Message>(){
                Label lblUserLeft = new Label();
                Label lblTextLeft = new Label();
                Label lblReplyLeft = new Label();
                Label lblReplyRight = new Label();
                VBox vBoxLeft = new VBox(lblReplyLeft, lblUserLeft, lblTextLeft);

                Label lblUserRight = new Label();
                Label lblTextRight = new Label();
                VBox vBoxRight = new VBox(lblReplyRight, lblTextRight, lblUserRight);
                {
                    vBoxLeft.setAlignment(Pos.CENTER_LEFT);
                    vBoxLeft.setPadding(new Insets(5));
                    vBoxRight.setAlignment(Pos.CENTER_RIGHT);
                    vBoxRight.setPadding(new Insets(5));
                    vBoxLeft.setStyle("-fx-background-image: url('/com/example/socialnetworkgui/images/message_background.jpg');");
                    vBoxRight.setStyle("-fx-background-image: url('/com/example/socialnetworkgui/images/message_background.jpg');");
                }

                @Override
                protected void updateItem(Message item, boolean empty) {
                    super.updateItem(item, empty);

                    if(empty)
                    {
                        setText(null);
                        setGraphic(null);
                    }
                    else{
                        if(item.getFrom().getId().equals(userDTO.getID()))
                        {
                            //lblUserRight.setText(":" + item.getFrom().getUserName());
                            lblTextRight.setText(item.getMessage());
                            lblReplyLeft.setVisible(false);
                            if(item.getReply() != null){
                                lblReplyRight.setText(String.format("Replied to:\n %s\n%s", item.getReply().getFrom().getUserName(), item.getReply().getMessage()));
                                lblReplyLeft.setVisible(true);
                            }
                            setGraphic(vBoxRight);
                        }
                        else{
                            lblUserLeft.setText(item.getFrom().getUserName() + ":");
                            lblTextLeft.setText(item.getMessage());
                            lblReplyLeft.setVisible(false);
                            if(item.getReply() != null) {
                                lblReplyLeft.setVisible(true);
                                lblReplyRight.setText(String.format("Replied to:\n %s\n%s", item.getReply().getFrom().getUserName(), item.getReply().getMessage()));
                            }
                            setGraphic(vBoxLeft);
                        }
                    }
                }

            };

            return cell;
        });

    }

    private void loadChatsList() {
        var ret = userDTO.getChattingUsers();
        chatsListData.clear();
        chatsList.setFixedCellSize(60);
        List<User> users = ret.getFirst();;
        List<Group> groups = ret.getSecond();
        users.forEach(user -> {
            if(user == null)
                return;
            EntityBox temp = new EntityBox(user);
            temp.setSize(150, 40);
            chatsListData.add(temp);
        });
        groups.forEach(group -> {
            if(group == null)
                return;
            chatsListData.add(new EntityBox(group));
        });
        chatsList.setItems(chatsListData);

    }

    private void loadAuthenticatedUserDetails() {
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/com/example/socialnetworkgui/images/user_icon.png")).toExternalForm());
        profilePicture.setImage(image);
        usernameLabel.setText(authenticatedUser.getUserName());
        fullNameLabel.setText(authenticatedUser.getFirstName() + " " + authenticatedUser.getLastName());
    }


    @Override
    public void update(ObservableEvent event) {
        switch (event.getType()) {
            case "messageEvent" -> messageEventHandle((MessageObservableEvent) event);
            case "groupEvent" -> groupEventHandle((GroupObservableEvent) event);
        }
    }

    private void groupEventHandle(GroupObservableEvent event) {
        Group group = event.getGroup();
        chatsListData.add(new EntityBox(group));
    }

    private void messageEventHandle(MessageObservableEvent messageEvent) {
        switch (currentChat.getSecond()) {
            case "group":
                // same group
                if (Objects.equals(messageEvent.getMessage().getGroupID(), currentChat.getFirst())) {
                    messageListData.add(messageEvent.getMessage());
                    messageList.scrollTo(messageListData.size() - 1);
                }
                break;
            case "user":
                // same user chat
                if (messageEvent.getMessage().getTo().size() == 2 && messageEvent.getMessage().sameParticipants(userDTO.getID(), currentChat.getFirst())) {
                    messageListData.add(messageEvent.getMessage());
                    messageList.scrollTo(messageListData.size() - 1);
                }
                break;
            default:
                break;
        }
    }
}
