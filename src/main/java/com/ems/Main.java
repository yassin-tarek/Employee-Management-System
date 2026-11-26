package com.ems;

import java.io.IOException;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        Navigator.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}