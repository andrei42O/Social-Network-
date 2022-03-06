module com.example.socialnetworkgui {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires org.apache.pdfbox;

    opens com.socialnetwork.AppGUI to javafx.fxml;
    exports com.socialnetwork.AppGUI;
    exports com.socialnetwork.models;
    opens com.socialnetwork.models to javafx.fxml;

    exports com.socialnetwork.AppGUI.DTO;
    opens com.socialnetwork.AppGUI.DTO to javafx.fxml;
    exports com.socialnetwork.AppGUI.Controllers;
    opens com.socialnetwork.AppGUI.Controllers to javafx.fxml;
}