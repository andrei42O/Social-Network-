package com.socialnetwork.AppGUI.Controllers;

import com.socialnetwork.AppGUI.DTO.UserDTO;
import com.socialnetwork.AppGUI.GUI;
import com.socialnetwork.domain.*;
import com.socialnetwork.service.GroupService;
import com.socialnetwork.service.MessageService;
import com.socialnetwork.service.ServiceFriendship;
import com.socialnetwork.service.UserService;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.xml.stream.events.StartDocument;
import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ReportsController {
    public CheckBox messageCheckBox;
    public CheckBox friendshipsCheckBox;
    public Button generateButton;
    public DatePicker startDate;
    public DatePicker endDate;

    private UserDTO userDTO;
    private UserService userService;
    private ServiceFriendship friendshipService;
    private MessageService messageService;
    private GroupService groupService;
    private Stage stage;

    public void setServices(UserDTO userDTO, UserService userService, ServiceFriendship friendshipService, MessageService messageService, GroupService groupService){
        this.userService = userService;
        this.friendshipService = friendshipService;
        this.messageService = messageService;
        this.groupService = groupService;
        this.userDTO = userDTO;
    }

    public void load(){
        assignSignals();
    }

    private void assignSignals() {
        generateButton.setOnAction(event -> {
            if(!messageCheckBox.isSelected() && !friendshipsCheckBox.isSelected()){
                GUI.alertUser("The report cannot be empty! Choose at least a log!");
                return;
            }
            if(startDate.getValue() == null || endDate.getValue() == null){
                GUI.alertUser("Pick the time interval!");
                return;
            }
            if (startDate.getValue().compareTo(endDate.getValue()) > 0){
                GUI.alertUser("Alege un interval orar normal!");
            }
            generatePDF();
        });
        friendshipsCheckBox.selectedProperty().addListener(new ChangeListener<>(){

            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue) {
                    if(!messageCheckBox.isSelected())
                        friendshipsCheckBox.setIndeterminate(false);
                }
            }
        });
    }

    private void generatePDF() {
        if (messageCheckBox.isSelected() && friendshipsCheckBox.isSelected())
            exportBoth();
        else if(messageCheckBox.isSelected()){
            exportConversation();
        }
        messageCheckBox.setSelected(false);
        friendshipsCheckBox.setSelected(false);
        startDate.setValue(null);
        endDate.setValue(null);
    }

    public void exportConversation() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(startDate.getScene().getWindow());
            writeMessagesToPdf(file + "\\mesage.pdf", userDTO.getMessagesIn(startDate.getValue(), endDate.getValue()));
        } catch (Exception e) {
            GUI.alertUser(e.getMessage());
        }
    }

    public void writeToPDF(String filePath, List<Friendship> friendships) {
        try (PDDocument doc = new PDDocument()) {
            PDFont font = PDType1Font.HELVETICA;
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.setFont(font, 12);
            int lines = 1;
            float pageHeight = page.getMediaBox().getHeight();
            for (Friendship currentFriendship : friendships) {
                int x = 0;
                content.beginText();
                content.newLineAtOffset(x, pageHeight - 50 * lines);
                x += x + 100;
                content.showText("From: " + userService.findOne(currentFriendship.getId1()).getFirstName() + " " +
                        userService.findOne(currentFriendship.getId1()).getLastName() + ", to: " +
                        userService.findOne(currentFriendship.getId2()).getFirstName() + " " +
                        userService.findOne(currentFriendship.getId2()).getLastName() + " " +
                        currentFriendship.getDate().toString());
                content.endText();
                ++lines;
                if (lines > 9) {
                    page = new PDPage();
                    doc.addPage(page);
                    content.close();
                    content = new PDPageContentStream(doc, page);
                    content.setFont(font, 12);
                }
            }
            content.close();
            doc.save(filePath);
        } catch (IOException ex) {
            GUI.alertUser("PDF couldn't generate...");
        }
    }

    @FXML
    public void exportBoth() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File file = directoryChooser.showDialog(startDate.getScene().getWindow());
            String pathPrietenie = file + "\\prietenii.pdf";
            String pathMessage = file + "\\mesage.pdf";
            writeToPDF(pathPrietenie, userDTO.getFriendships(startDate.getValue(), endDate.getValue()));
            writeMessagesToPdf(pathMessage, userDTO.getMessagesIn(startDate.getValue(), endDate.getValue()));
        } catch (Exception e) {
            GUI.alertUser(e.getMessage());
        }

    }

    public void writeMessagesToPdf(String filePath, List<Message> valuesToExport) {
        try (PDDocument doc = new PDDocument()) {
            PDFont font = PDType1Font.TIMES_ROMAN;
            PDPage page = new PDPage();
            doc.addPage(page);
            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.setFont(font, 11);
            int lines = 1;
            float pageHeight = page.getMediaBox().getHeight();

            for (Message currentMessage : valuesToExport) {
                int startX = 0;

                content.beginText();
                content.newLineAtOffset(startX, pageHeight - 50 * lines);
                startX += startX + 100;
                StringBuilder toList = new StringBuilder("");
                for (Long userId : currentMessage.getTo().stream().map(Entity::getId).collect(Collectors.toList()))
                    toList.append(userService.findOne(userId).getFirstName()).append(" ").append(userService.findOne(userId).getLastName()).append(", ");
                String line = "From: " + currentMessage.getFrom().getFirstName() + " " +
                        currentMessage.getFrom().getLastName() + ", to: " +
                        toList + "MSG: " +
                        currentMessage.getMessage().replace("\n", "").replace("\r", "").replaceAll("\t", " ") + " " + currentMessage.getDate();
                content.showText(line.replace("\n", "").replace("\r", "").replaceAll("\t", " "));
                content.endText();
                lines += 1;
                if (lines > 9) {
                    page = new PDPage();
                    doc.addPage(page);
                    content.close();
                    content = new PDPageContentStream(doc, page);
                    content.setFont(font, 11);
                }
            }
            content.close();
            doc.save(filePath);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
