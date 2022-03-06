package com.socialnetwork;

import com.socialnetwork.AppGUI.GUI;
import com.socialnetwork.domain.*;
import com.socialnetwork.domain.validators.FriendshipValidator;
import com.socialnetwork.domain.validators.UserValidator;
import com.socialnetwork.repository.db.FriendRequestDbRepository;
import com.socialnetwork.repository.db.FriendshipDbRepository;
import com.socialnetwork.repository.db.MessageDbRepository;
import com.socialnetwork.repository.db.UserDbRepository;
import com.socialnetwork.service.ServiceFriendship;
import com.socialnetwork.service.UserService;
import com.socialnetwork.tests.Tests;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.controlsfx.control.action.Action;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        //Tests.run();
        GUI gui = new GUI();
        gui.run(args);
    }
}
