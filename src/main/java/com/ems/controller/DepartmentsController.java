package com.ems.controller;

import com.ems.AppContext;
import com.ems.Avatars;
import com.ems.Dialogs;
import com.ems.manager.DepartmentManager;
import com.ems.model.Department;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Departments page: add, rename (inline) and delete departments. */
public class DepartmentsController {

    @FXML private TextField newNameField;
    @FXML private Label statusMessage, listTitle;
    @FXML private VBox departmentRows;

    // ID of the department being renamed right now (-1 = none)
    private int editingId = -1;

    // ======================================================================
    // setup
    // ======================================================================

    @FXML
    private void initialize() {
        hideMessage();
        refresh();
    }

    // Rebuilds the whole list from the managers' current data
    private void refresh() {
        departmentRows.getChildren().clear();

        DepartmentManager departments = AppContext.departments();
        int total = departments.getAllDepartments().size();

        listTitle.setText("All Departments (" + total + ")");

        if (total == 0) {
            Label empty = new Label("No departments yet. Add your first one above.");
            empty.getStyleClass().add("placeholder-text");
            departmentRows.getChildren().add(empty);
            return;
        }

        for (Department d : departments.getAllDepartments()) {
            departmentRows.getChildren().add(buildRow(d));
        }
    }

    private Node buildRow(Department d) {
        HBox row = new HBox(14);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("dept-row");

        Label icon = new Label(Avatars.initialOf(d.getName()));
        icon.getStyleClass().add("dept-icon");

        if (d.getId() == editingId) {
            // ----- rename mode: text field + Save / Cancel -----
            TextField field = new TextField(d.getName());
            HBox.setHgrow(field, Priority.ALWAYS);
            field.setOnAction(event -> saveRename(d, field.getText()));
            field.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    cancelRename();
                }
            });

            Button save = new Button("Save");
            save.getStyleClass().addAll("primary-button", "small-button");
            save.setOnAction(event -> saveRename(d, field.getText()));

            Button cancel = new Button("Cancel");
            cancel.getStyleClass().addAll("secondary-button", "small-button");
            cancel.setOnAction(event -> cancelRename());

            row.getChildren().addAll(icon, field, save, cancel);

            Platform.runLater(() -> {
                field.requestFocus();
                field.selectAll();
            });
        } else {
            // ----- normal mode: name, employee count, Rename / Delete -----
            int count = AppContext.employees().countInDepartment(d.getName());

            Label name = new Label(d.getName());
            name.getStyleClass().add("dept-title");
            Label meta = new Label(count + (count == 1 ? " employee" : " employees"));
            meta.getStyleClass().add("dept-meta");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button rename = new Button("Rename");
            rename.getStyleClass().addAll("action-button", "action-edit");
            rename.setOnAction(event -> startRename(d));

            Button delete = new Button("Delete");
            delete.getStyleClass().addAll("action-button", "action-delete");
            delete.setOnAction(event -> onDelete(d));

            row.getChildren().addAll(icon, new VBox(2, name, meta), spacer, rename, delete);
        }

        return row;
    }

    // ======================================================================
    // add
    // ======================================================================

    @FXML
    private void onAdd() {
        hideMessage();

        String name = (newNameField.getText() == null) ? "" : newNameField.getText().trim();

        if (name.isEmpty()) {
            showError("Please enter a department name.");
            return;
        }
        if (AppContext.departments().getByName(name) != null) {
            showError("A department named \"" + name + "\" already exists.");
            return;
        }

        if (AppContext.departments().addDepartment(name)) {
            newNameField.clear();
            editingId = -1;
            refresh();
            showSuccess("Department \"" + name + "\" was added.");
        } else {
            showError("Could not save the department. Make sure the data folder can be written to.");
        }
    }

    // ======================================================================
    // rename
    // ======================================================================

    private void startRename(Department d) {
        hideMessage();
        editingId = d.getId();
        refresh();
    }

    private void cancelRename() {
        hideMessage();
        editingId = -1;
        refresh();
    }

    private void saveRename(Department d, String rawName) {
        hideMessage();

        String newName = (rawName == null) ? "" : rawName.trim();

        if (newName.isEmpty()) {
            showError("The department name cannot be empty.");
            return;
        }
        if (newName.equals(d.getName())) {
            cancelRename();                      // nothing changed
            return;
        }

        Department other = AppContext.departments().getByName(newName);
        if (other != null && other.getId() != d.getId()) {
            showError("A department named \"" + newName + "\" already exists.");
            return;
        }

        String oldName = d.getName();

        if (!AppContext.departments().renameDepartment(d.getId(), newName)) {
            showError("Could not rename the department.");
            return;
        }

        // Employees store the department NAME, so they must be updated too
        if (!AppContext.employees().renameDepartment(oldName, newName)) {
            AppContext.departments().renameDepartment(d.getId(), oldName);   // undo
            showError("Could not update the employees of this department, so nothing was changed.");
            return;
        }

        editingId = -1;
        refresh();
        showSuccess("\"" + oldName + "\" was renamed to \"" + newName + "\".");
    }

    // ======================================================================
    // delete
    // ======================================================================

    private void onDelete(Department d) {
        hideMessage();

        int count = AppContext.employees().countInDepartment(d.getName());
        if (count > 0) {
            showError("\"" + d.getName() + "\" still has " + count
                    + (count == 1 ? " employee" : " employees")
                    + ". Move or delete them before deleting the department.");
            return;
        }

        boolean confirmed = Dialogs.confirmDelete(
                departmentRows.getScene().getWindow(),
                "Delete " + d.getName() + "?",
                "This department will be removed. This cannot be undone.");

        if (!confirmed) {
            return;
        }

        if (AppContext.departments().deleteDepartment(d.getId())) {
            editingId = -1;
            refresh();
            showSuccess("Department \"" + d.getName() + "\" was deleted.");
        } else {
            showError("Could not delete the department. Make sure the data folder can be written to.");
        }
    }

    // ======================================================================
    // message banner
    // ======================================================================

    private void showError(String text) {
        showMessage(text, "banner-error");
    }

    private void showSuccess(String text) {
        showMessage(text, "banner-success");
    }

    private void showMessage(String text, String styleClass) {
        statusMessage.getStyleClass().removeAll("banner-error", "banner-success");
        statusMessage.getStyleClass().add(styleClass);
        statusMessage.setText(text);
        statusMessage.setVisible(true);
        statusMessage.setManaged(true);
    }

    private void hideMessage() {
        statusMessage.setText("");
        statusMessage.setVisible(false);
        statusMessage.setManaged(false);
    }
}