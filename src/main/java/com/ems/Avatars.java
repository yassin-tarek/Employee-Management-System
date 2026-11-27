package com.ems;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.ems.model.Employee;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;

/** Builds the round avatar of an employee: the profile picture, or the first letter of the name. */
public final class Avatars {

    private Avatars() {
    }

    public static Node create(Employee employee, double size) {
        StackPane avatar = new StackPane();
        avatar.getStyleClass().add("avatar-circle");
        avatar.setMinSize(size, size);
        avatar.setPrefSize(size, size);
        avatar.setMaxSize(size, size);

        Image image = loadImage(employee.getImagePath(), size * 3);

        if (image != null) {
            // Crop to a centered square, then clip to a circle
            double side = Math.min(image.getWidth(), image.getHeight());
            ImageView view = new ImageView(image);
            view.setViewport(new Rectangle2D(
                    (image.getWidth() - side) / 2, (image.getHeight() - side) / 2, side, side));
            view.setFitWidth(size);
            view.setFitHeight(size);
            view.setClip(new Circle(size / 2, size / 2, size / 2));
            avatar.getChildren().add(view);
        } else {
            Label initial = new Label(initialOf(employee.getName()));
            initial.getStyleClass().add("avatar-circle-text");
            initial.setStyle("-fx-font-size: " + Math.round(size * 0.4) + "px;");
            avatar.getChildren().add(initial);
        }

        return avatar;
    }

    public static String initialOf(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "?";
        }
        return name.trim().substring(0, 1).toUpperCase();
    }

    // Returns null if there is no picture, the file is missing, or it is not a valid image
    private static Image loadImage(String path, double maxSize) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        try {
            Path file = Paths.get(path);
            if (!Files.exists(file)) {
                return null;
            }
            Image image = new Image(file.toUri().toString(), maxSize, maxSize, true, true);
            return image.isError() ? null : image;
        } catch (RuntimeException ex) {
            return null;
        }
    }
}