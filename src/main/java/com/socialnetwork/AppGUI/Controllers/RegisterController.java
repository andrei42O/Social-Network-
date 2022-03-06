package com.socialnetwork.AppGUI.Controllers;

import com.socialnetwork.AppGUI.GUI;
import com.socialnetwork.AppGUI.MainWindowGUI;
import com.socialnetwork.domain.User;
import com.socialnetwork.service.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RegisterController {
    @FXML
    public Label dateLabel;
    @FXML
    public ImageView animatedImage;
    @FXML
    public Hyperlink alreadyUserLink;
    @FXML
    public TextField usernameInput;
    @FXML
    public PasswordField passwordInput;
    @FXML
    public Button registerButton;
    @FXML
    public TextField lastNameInput;
    public TextField firstNameInput;
    public TextField confirmPasswordInput;
    public AnchorPane registerPane;
    public Label invisibleError;

    UserService userService;
    ServiceFriendship friendshipService;
    MessageService messageService;
    GroupService groupService;
    private Stage stage;
    private SecurityService security;
    private EventService eventService;

    public void setServices(UserService userService, ServiceFriendship friendshipService, MessageService messageService, SecurityService security, EventService eventService) {
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.security = security;
        this.eventService = eventService;
    }

    public void load(){
        loadImage(animatedImage);
        loadDate();
        adjustStyle();
        assignSignals();
    }

    private void loadDate() {
        String pattern = "dd MMMMM yyyy";
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(pattern, Locale.US);
        String date = simpleDateFormat.format(new Date());
        dateLabel.setText(date);
    }

    private void adjustStyle() {
        invisibleError.setWrapText(true);
        dateLabel.setTextAlignment(TextAlignment.CENTER);
    }

    private void assignSignals() {
        confirmPasswordInput.textProperty().addListener(new ChangeListener<String>(){
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (!newValue.equals(passwordInput.getText())) {
                    semnalInvalidInput("The two passwords should match!");
                } else {
                    if(newValue.length() > 0)
                        semnalInvalidInput("");
                }
            }
        });

        registerButton.setOnAction(event -> {
            String firstName = firstNameInput.getText();
            String lastName = lastNameInput.getText();
            String username = usernameInput.getText();
            String password = hashPass(passwordInput.getText());
            validateData(firstName, lastName, username, password);
            User authenticatedUser = null;
            try {
                authenticatedUser = security.registerUser(firstName, lastName, username, password);
                if(authenticatedUser == null){
                    GUI.alertUser("There was a problem when registering this user!");
                }
                new MainWindowGUI(authenticatedUser, userService, friendshipService, messageService, groupService, eventService, security).show();
                GUI.closeWindow(this.stage);
            } catch (Exception e){
                e.printStackTrace();
                semnalInvalidInput(e.getMessage());
                clearLabels();
            }
        });
        alreadyUserLink.setOnAction(event -> {
            try {
                loadLoginWindow();
            } catch (IOException e) {
                GUI.alertUser("The login window cannot be opened!");
            }
        });
    }

    private String hashPass(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger no = new BigInteger(1, messageDigest);
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearLabels() {
        firstNameInput.clear();
        lastNameInput.clear();
        usernameInput.clear();
        passwordInput.clear();
        confirmPasswordInput.clear();
    }

    private void loadLoginWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetworkgui/login_window.fxml"));
        Pane root = loader.load();
        LoginController controller = loader.getController();
        controller.setServices(userService, friendshipService, messageService, groupService, security, eventService);
        controller.setStage(this.stage);
        controller.load();
        Scene scene = new Scene(root);
        this.stage.setScene(scene);
    }

    private void validateData(String firstName, String lastName, String username, String password) {
        if(firstName.length() == 0 &&
        lastName.length() == 0 &&
        username.length() == 0 &&
        password.length() == 0) {
            semnalInvalidInput("Please fill the information required!");
            return;
        }

    }

    private void semnalInvalidInput(String error) {
        invisibleError.setText(error);
        invisibleError.setStyle("-fx-text-fill: red;");
    }

    private void loadImage(ImageView img) {
        String[] colorHex = {"#c2fafa", "#b7ffff", "#a1c7dc"};
        Random rand = new Random();
        int randomNumber = rand.nextInt(3);
        String imgSource =  String.format("/com/example/socialnetworkgui/images/login_gif%s.gif", randomNumber);
        String backgroundColor = colorHex[randomNumber];
        Image i = new Image(Objects.requireNonNull(getClass().getResource(imgSource)).toExternalForm());
        img.setImage(i);
        registerPane.setStyle(String.format("-fx-background-color: %s;", backgroundColor));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
