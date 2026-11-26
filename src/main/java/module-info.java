module com.ems {
    requires transitive javafx.controls;
    requires javafx.fxml;

    // FXMLLoader needs reflective access to the controllers (@FXML private fields)
    opens com.ems to javafx.fxml;
    opens com.ems.controller to javafx.fxml;

    // TableView's PropertyValueFactory reads the model getters by reflection
    opens com.ems.model to javafx.base;

    exports com.ems;
}