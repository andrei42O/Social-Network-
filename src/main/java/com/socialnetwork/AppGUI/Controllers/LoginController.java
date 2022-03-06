package com.socialnetwork.AppGUI.Controllers;

import com.socialnetwork.AppGUI.GUI;
import com.socialnetwork.AppGUI.MainWindowGUI;
import com.socialnetwork.AppGUI.WelcomeWindowGUI;
import com.socialnetwork.domain.User;
import com.socialnetwork.repository.db.EventRepository;
import com.socialnetwork.service.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class LoginController {

    public ImageView animatedImage;
    public AnchorPane loginPane;
    public Label dateLabel;
    public TextField usernameInput;
    public PasswordField passwordInput;
    public Hyperlink registerLink;
    public Hyperlink forgotPassword;
    public Label hiddenLabel;
    @FXML
    private Button loginButton;

    UserService userService;
    ServiceFriendship friendshipService;
    MessageService messageService;
    GroupService groupService;
    SecurityService security;
    private Stage stage;
    private EventService eventService;


    public void setServices(UserService us, ServiceFriendship fs, MessageService messageService, GroupService groupService, SecurityService security, EventService eventService){
        this.userService = us;
        this.friendshipService = fs;
        this.messageService = messageService;
        this.groupService = groupService;
        this.eventService = eventService;
        this.security = security;
    }

    public void load(){
//        __gotoMainWindow();
        loadImage(animatedImage);
        loadDate();
        assignSignals();
        hiddenLabel.setVisible(false);
        if(!this.stage.isShowing())
            this.stage.show();
    }

    private void __gotoMainWindow() {
        User tempAuthenticated = security.login("aNdrei42O", "covrig");
        try {
            new MainWindowGUI(tempAuthenticated, userService, friendshipService, messageService, groupService, eventService, security);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDate() {
        String pattern = "dd MMMMM yyyy";
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat(pattern, Locale.US);

        String date = simpleDateFormat.format(new Date());
        dateLabel.setText(date);
    }

    public void loadRegisterWindow() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/example/socialnetworkgui/register_window.fxml"));
        Pane root = loader.load();
        RegisterController controller = loader.getController();
        controller.setServices(userService, friendshipService, messageService, security, eventService);
        controller.setStage(this.stage);
        controller.load();
        Scene scene = new Scene(root);
        this.stage.setScene(scene);
        //scene.getStylesheets().addAll("src/main/resources/com/example/socialnetworkgui/CSS/main_window.css", "src/main/resources/com/example/socialnetworkgui/CSS/login_page.css");
    }

    private void assignSignals() {
        loginButton.setOnAction(event -> {
            String username = usernameInput.getText();
            String password = passwordInput.getText();
            User authenticatedUser = security.login(username, password);
            if(authenticatedUser != null){
                try {
                    new MainWindowGUI(authenticatedUser, userService, friendshipService, messageService, groupService, eventService, security).show();
                    GUI.closeWindow(event);
                } catch (Exception e) {
                    GUI.alertUser("The main window couldn't be opened!");
                }
            } else {
                hiddenLabel.setText("incorrect username and password combination");
                hiddenLabel.setStyle("-fx-text-fill: red;");
                hiddenLabel.setWrapText(true);
                hiddenLabel.setVisible(true);
            }
        });
        registerLink.setOnAction(event ->{
            System.out.println("lasldasldlaslda");
            try {
                loadRegisterWindow();
            } catch (IOException e) {
                GUI.alertUser("Register window cannot be loaded!\n");
            }
        });
//        loginButton.setOnMouseEntered(e -> loginButton.setStyle(HOVERED_BUTTON_STYLE));
//        loginButton.setOnMouseExited(e -> loginButton.setStyle(IDLE_BUTTON_STYLE));
    }


    private void loadImage(ImageView img) {
        String[] colorHex = {"#c2fafa", "#b7ffff", "#a1c7dc"};
        Random rand = new Random();
        int randomNumber = rand.nextInt(3);
        String imgSource =  String.format("/com/example/socialnetworkgui/images/login_gif%s.gif", randomNumber);
        String backgroundColor = colorHex[randomNumber];
        Image i = new Image(Objects.requireNonNull(getClass().getResource(imgSource)).toExternalForm());
        img.setImage(i);
        loginPane.setStyle(String.format("-fx-background-color: %s;", backgroundColor));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
