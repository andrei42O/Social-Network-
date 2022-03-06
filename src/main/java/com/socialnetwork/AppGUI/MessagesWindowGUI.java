package com.socialnetwork.AppGUI;

import com.socialnetwork.AppGUI.Controllers.MessageController;
import com.socialnetwork.domain.User;
import com.socialnetwork.models.MessageModel;
import com.socialnetwork.models.UserModel;
import com.socialnetwork.service.GroupService;
import com.socialnetwork.service.MessageService;
import com.socialnetwork.service.ServiceFriendship;
import com.socialnetwork.service.UserService;
import com.socialnetwork.tools.events.MessageObservableEvent;
import com.socialnetwork.tools.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessagesWindowGUI extends Stage implements Observer<MessageObservableEvent> {
    UserService userService;
    ServiceFriendship friendshipService;
    MessageService messageService;
    User authenticatedUser;
    GroupService groupService;

    ListView<MessageModel> messages;
    ListView<UserModel> friends;
    private final ObservableList<MessageModel> messagesList = FXCollections.observableArrayList();
    private final ObservableList<UserModel> friendsList = FXCollections.observableArrayList();

    TextArea textArea;
    Button sendMessageButton;

    public MessagesWindowGUI(UserService userService, ServiceFriendship friendshipService, MessageService messageService, GroupService groupService, User authenticatedUser) throws Exception {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.authenticatedUser = authenticatedUser;
        this.messageService = messageService;
        this.groupService = groupService;
        loadGUI();
    }

    private void loadGUI() throws Exception {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetworkgui/message_window.fxml"));
        Pane root = loader.load();
        MessageController controller = loader.getController();
        controller.setServices(userService, friendshipService, messageService, groupService, authenticatedUser);
        controller.setStage(stage);
        controller.load();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("YOLO Communications");
        stage.show();



//        Pane root = new Pane();
//        Scene scene = new Scene(root, 790, 500);
//        loadFriendsDisplay();
//        loadMessageDisplay();
//        loadFriends();
//        loadTextAndSend();
//        assignSignals();
//        Label loginLabel = new Label(String.format("Logged as: %s", authenticatedUser.getUserName()));
//        loginLabel.setMinHeight(30);
//        loginLabel.setMaxWidth(Double.MAX_VALUE);
//        loginLabel.setAlignment(Pos.CENTER);
//        ImageView userLogo = new ImageView(new Image("file:src/images/user_icon.png"));
//        userLogo.setFitHeight(30);
//        userLogo.setFitWidth(30);
//        layout = new HBox(new VBox(new HBox(userLogo, loginLabel), friends), new VBox(messages, new HBox(textArea, sendMessageButton)));
//        root.getChildren().add(layout);
//        this.setTitle("Messages");
//        this.setScene(scene);
    }

    private void assignSignals() {
        this.focusedProperty().addListener(x -> {
            if(friends.getSelectionModel().getSelectedItem() == null)
                return;
            updateChat(Long.parseLong(friends.getSelectionModel().getSelectedItem().getHiddenID()));
        });
        friends.setOnMouseClicked(event -> {
            if(friends.getSelectionModel().isEmpty())
                return;
            updateChat(Long.parseLong(friends.getSelectionModel().getSelectedItem().getHiddenID()));
            messages.scrollTo(messagesList.size());
            textArea.clear();
        });
        textArea.setOnKeyPressed(event -> {
            if(textArea.isFocused() && event.getCode().equals(KeyCode.ENTER)){
                sendMessageGUI();
            }
        });
    }

    private void sendMessageGUI() {
        try {
            Long replyID = null;
            if(!messages.getSelectionModel().getSelectedIndices().isEmpty())
                replyID = messages.getSelectionModel().getSelectedItem().getHiddenID();
            messageService.sendMessage(authenticatedUser.getId(), textArea.getText(), replyID, Long.parseLong(friends.getSelectionModel().getSelectedItem().getHiddenID()));
        } catch(Exception e){
            GUI.alertUser(e.getMessage());
        }
    }

    private void updateChat(long otherParticipantID) {
        messagesList.clear();
        try {
            messagesList.setAll(StreamSupport.stream(messageService.showConversation(authenticatedUser.getId(), otherParticipantID).spliterator(), false).map(MessageModel::new).collect(Collectors.toList()));
        } catch (Exception ignored) {
            // there are no messages in this conversation
        }
    }

    private void loadFriendsDisplay() {
        friends = new ListView<>();
        friends.setEditable(true);
        loadFriends();
        friends.setItems(friendsList);
    }

    private void loadFriends() {
        try {
            friendsList.setAll(StreamSupport.stream(friendshipService.getFriends(authenticatedUser.getId()).spliterator(), false).map(x -> new UserModel(x.getFirst())).collect(Collectors.toList()));
        } catch (Exception ignored){
            // no friends => no one to chat ;-(
        }
    }

    private void loadTextAndSend() {
        textArea = new TextArea();
        textArea.setPromptText("Your message");
        sendMessageButton = new Button();
        ImageView sendMessageImage = new ImageView(new Image("file:src/images/send_message_button.png"));
        sendMessageImage.setFitHeight(50);
        sendMessageImage.setFitWidth(50);
        sendMessageImage.setPreserveRatio(true);
        sendMessageButton.setBackground(null);
        sendMessageButton.setGraphic(sendMessageImage);
    }

    private void loadMessageDisplay() {
        messages = new ListView<MessageModel>();
        messages.setEditable(true);
        // loadMessages(); // load all existing messages
        messages.setItems(messagesList);
    }

    private void loadMessages() {
        messagesList.setAll (StreamSupport.stream(messageService.getAllMessages().spliterator(), false).map(msg -> new MessageModel(msg.getId(), msg.getFrom(), msg.getTo(), msg.getMessage(), msg.getReply(), msg.getDate())).collect(Collectors.toList()));
    }


    @Override
    public void update(MessageObservableEvent messageEvent) {
        messagesList.add(new MessageModel(messageEvent.getMessage()));
        messages.scrollTo(messagesList.size());
        textArea.clear();
    }
}
