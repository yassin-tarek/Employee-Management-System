package com.ems;

import java.io.IOException;
import java.net.URL;

import com.ems.controller.ShellController;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Switches between Login and the main window, and between pages inside the main window.
 * The main window ("Shell") holds the sidebar + header; only its center content changes.
 */
public final class Navigator {

    public enum Page {
        DASHBOARD("Dashboard", "Dashboard"),
        ADD_EMPLOYEE("AddEmployee", "Add Employee"),
        EMPLOYEE_LIST("EmployeeList", "Employees"),
        EMPLOYEE_PROFILE("EmployeeProfile", "Employee Profile"),
        DEPARTMENTS("Departments", "Departments");

        private final String fileName;
        private final String title;

        Page(String fileName, String title) {
            this.fileName = fileName;
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    private static final String VIEW_PATH = "/com/ems/view/";
    private static final String CSS_PATH = "/com/ems/css/";

    private static Scene scene;
    private static ShellController shell;

    private Navigator() {
    }

    // ---------- start ----------

    public static void start(Stage stage) throws IOException {
        Parent root = newLoader("Login").load();

        scene = new Scene(root, 1280, 800);

        URL css = Navigator.class.getResource(CSS_PATH + "style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }

        stage.setTitle("Employee Management System");
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.setScene(scene);
        stage.show();
    }

    // ---------- login <-> main window ----------

    public static void showLogin() {
        try {
            shell = null;
            Parent root = newLoader("Login").load();
            scene.setRoot(root);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load the Login view.", ex);
        }
    }

    public static void showShell() {
        try {
            FXMLLoader loader = newLoader("Shell");
            Parent root = loader.load();
            shell = loader.getController();
            scene.setRoot(root);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not load the Shell view.", ex);
        }
    }

    // ---------- pages ----------

    public static void goTo(Page page) {
        if (shell == null) {
            return;
        }

        Node content;
        try {
            content = newLoader(page.fileName).load();
        } catch (IOException | RuntimeException ex) {
            // Lets the app run while some pages are still empty
            System.err.println("Could not load " + page.fileName + ".fxml: " + ex.getMessage());
            content = placeholder(page);
        }

        shell.setContent(content, page);
    }

    // ---------- helpers ----------

    private static FXMLLoader newLoader(String name) throws IOException {
        URL url = Navigator.class.getResource(VIEW_PATH + name + ".fxml");
        if (url == null) {
            throw new IOException("Missing view file: " + name + ".fxml");
        }
        return new FXMLLoader(url);
    }

    private static Node placeholder(Page page) {
        Label title = new Label(page.getTitle());
        title.getStyleClass().add("placeholder-title");

        Label text = new Label("This page has not been built yet.");
        text.getStyleClass().add("placeholder-text");

        VBox box = new VBox(8, title, text);
        box.setAlignment(Pos.CENTER);
        return box;
    }
}