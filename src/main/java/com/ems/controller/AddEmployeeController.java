package com.ems.controller;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class AddEmployeeController {

    @FXML
    private ImageView imgProfile;

    private File selectedImageFile;

    @FXML
    private void handleChooseImage() {

        FileChooser fileChooser = new FileChooser();

        fileChooser.setTitle("Choose Employee Profile Picture");

        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(
                "Image Files",
                "*.png",
                "*.jpg",
                "*.jpeg"
            )
        );

        File file = fileChooser.showOpenDialog(
            imgProfile.getScene().getWindow()
        );

        if (file != null) {

            selectedImageFile = file;

            Image image = new Image(file.toURI().toString());

            imgProfile.setImage(image);
        }
    }
}