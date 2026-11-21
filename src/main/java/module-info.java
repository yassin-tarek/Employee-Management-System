module com.ems {
    requires javafx.fxml;
    requires transitive javafx.controls;
    opens com.ems to javafx.fxml;
    exports com.ems;
}
