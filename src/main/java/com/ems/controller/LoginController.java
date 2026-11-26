package com.ems.controller;

import com.ems.Navigator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private void initialize() {
        hideError();

        // Hide the error message as soon as the user starts typing again
        usernameField.textProperty().addListener((obs, oldValue, newValue) -> hideError());
        passwordField.textProperty().addListener((obs, oldValue, newValue) -> hideError());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        if (USERNAME.equals(username) && PASSWORD.equals(password)) {
            Navigator.showShell();
            Navigator.goTo(Navigator.Page.DASHBOARD);
        } else {
            showError("Incorrect username or password.");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}