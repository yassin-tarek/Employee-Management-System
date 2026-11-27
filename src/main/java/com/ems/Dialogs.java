package com.ems;

import java.net.URL;
import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Window;

/** Confirmation dialogs that use the app's look. Shared by every page that deletes something. */
public final class Dialogs {

    private Dialogs() {
    }

    /** Shows a "Delete / Cancel" dialog. Returns true only if the user pressed Delete. */
    public static boolean confirmDelete(Window owner, String header, String message) {
        ButtonType deleteType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(deleteType, cancelType);
        alert.setTitle("Confirm delete");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.setGraphic(null);
        if (owner != null) {
            alert.initOwner(owner);
        }

        DialogPane pane = alert.getDialogPane();
        URL css = Dialogs.class.getResource("/com/ems/css/style.css");
        if (css != null) {
            pane.getStylesheets().add(css.toExternalForm());
        }
        pane.lookupButton(deleteType).getStyleClass().add("danger-button");
        pane.lookupButton(cancelType).getStyleClass().add("secondary-button");

        Optional<ButtonType> answer = alert.showAndWait();
        return answer.isPresent() && answer.get() == deleteType;
    }
}