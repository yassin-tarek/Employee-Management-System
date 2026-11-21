module com.ems {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.ems to javafx.fxml;
    exports com.ems;
}
