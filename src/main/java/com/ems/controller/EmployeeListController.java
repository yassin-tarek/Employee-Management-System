package com.ems.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.ems.AppContext;
import com.ems.Avatars;
import com.ems.Navigator;
import com.ems.Navigator.Page;
import com.ems.model.Employee;
import com.ems.model.EmployeeType;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/** Employees page: search, filter, and View / Edit / Delete for every employee. */
public class EmployeeListController {

    private static final String ALL_DEPARTMENTS = "All Departments";
    private static final String ALL_TYPES = "All Types";
    private static final String FULL_TIME = "Full-Time";
    private static final String PART_TIME = "Part-Time";

    @FXML private TextField searchField;
    @FXML private ComboBox<String> departmentFilter, typeFilter;
    @FXML private Label statusMessage, countLabel;

    @FXML private TableView<Employee> table;
    @FXML private TableColumn<Employee, Employee> employeeCol, actionsCol;
    @FXML private TableColumn<Employee, Integer> idCol;
    @FXML private TableColumn<Employee, String> departmentCol, positionCol;
    @FXML private TableColumn<Employee, EmployeeType> typeCol;
    @FXML private TableColumn<Employee, Double> salaryCol;

    // The rows currently shown (after search + filters)
    private final ObservableList<Employee> visible = FXCollections.observableArrayList();

    // ======================================================================
    // setup
    // ======================================================================

    @FXML
    private void initialize() {
        hideMessage();

        // Filters
        departmentFilter.getItems().add(ALL_DEPARTMENTS);
        departmentFilter.getItems().addAll(AppContext.departments().getNames());
        departmentFilter.setValue(ALL_DEPARTMENTS);

        typeFilter.getItems().setAll(ALL_TYPES, FULL_TIME, PART_TIME);
        typeFilter.setValue(ALL_TYPES);

        // Table: sorting is done by clicking the column headers
        SortedList<Employee> sorted = new SortedList<>(visible);
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);

        setupColumns();

        // Double-click a row to open the profile
        table.setRowFactory(tv -> {
            TableRow<Employee> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    onView(row.getItem());
                }
            });
            return row;
        });

        // Refresh whenever the search text or a filter changes
        searchField.textProperty().addListener((obs, o, n) -> refresh());
        departmentFilter.valueProperty().addListener((obs, o, n) -> refresh());
        typeFilter.valueProperty().addListener((obs, o, n) -> refresh());

        refresh();

        // Start sorted by ID
        table.getSortOrder().add(idCol);
    }

    private void setupColumns() {
        // Employee: avatar + name + email
        employeeCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<Employee>(d.getValue()));
        employeeCol.setComparator((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        employeeCol.setCellFactory(col -> new TableCell<Employee, Employee>() {
            @Override
            protected void updateItem(Employee e, boolean empty) {
                super.updateItem(e, empty);
                if (empty || e == null) {
                    setGraphic(null);
                    return;
                }
                Label name = new Label(e.getName());
                name.getStyleClass().add("cell-name");
                Label email = new Label(e.getEmail() == null ? "" : e.getEmail());
                email.getStyleClass().add("cell-sub");

                HBox box = new HBox(12, Avatars.create(e, 40), new VBox(2, name, email));
                box.setAlignment(Pos.CENTER_LEFT);
                setGraphic(box);
            }
        });

        idCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<Integer>(d.getValue().getId()));
        departmentCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<String>(d.getValue().getDepartment()));
        positionCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<String>(d.getValue().getPosition()));

        // Type: colored badge
        typeCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<EmployeeType>(d.getValue().getEmployeeType()));
        typeCol.setCellFactory(col -> new TableCell<Employee, EmployeeType>() {
            @Override
            protected void updateItem(EmployeeType type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setGraphic(null);
                    return;
                }
                boolean fullTime = (type == EmployeeType.FULL_TIME);
                Label badge = new Label(fullTime ? FULL_TIME : PART_TIME);
                badge.getStyleClass().addAll("badge", fullTime ? "badge-fulltime" : "badge-parttime");
                setGraphic(badge);
            }
        });

        // Salary
        salaryCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<Double>(d.getValue().calculateSalary()));
        salaryCol.setCellFactory(col -> new TableCell<Employee, Double>() {
            @Override
            protected void updateItem(Double salary, boolean empty) {
                super.updateItem(salary, empty);
                if (empty || salary == null) {
                    setText(null);
                    return;
                }
                setText(String.format("%,.2f", salary));
                getStyleClass().remove("cell-salary");
                getStyleClass().add("cell-salary");
            }
        });

        // Actions: View / Edit / Delete
        actionsCol.setCellValueFactory(d -> new ReadOnlyObjectWrapper<Employee>(d.getValue()));
        actionsCol.setCellFactory(col -> new TableCell<Employee, Employee>() {
            private final Button view = new Button("View");
            private final Button edit = new Button("Edit");
            private final Button delete = new Button("Delete");
            private final HBox box = new HBox(6, view, edit, delete);

            {
                view.getStyleClass().addAll("action-button", "action-view");
                edit.getStyleClass().addAll("action-button", "action-edit");
                delete.getStyleClass().addAll("action-button", "action-delete");
                box.setAlignment(Pos.CENTER_LEFT);

                view.setOnAction(event -> onView(getItem()));
                edit.setOnAction(event -> onEdit(getItem()));
                delete.setOnAction(event -> onDelete(getItem()));
            }

            @Override
            protected void updateItem(Employee e, boolean empty) {
                super.updateItem(e, empty);
                setGraphic(empty || e == null ? null : box);
            }
        });
    }

    // ======================================================================
    // search + filters
    // ======================================================================

    private void refresh() {
        String key = searchField.getText() == null ? "" : searchField.getText().trim();
        String department = departmentFilter.getValue();
        String type = typeFilter.getValue();

        List<Employee> result = new ArrayList<>();

        for (Employee e : AppContext.employees().searchEmployees(key)) {

            if (department != null && !ALL_DEPARTMENTS.equals(department)
                    && !department.equalsIgnoreCase(e.getDepartment())) {
                continue;
            }
            if (FULL_TIME.equals(type) && e.getEmployeeType() != EmployeeType.FULL_TIME) {
                continue;
            }
            if (PART_TIME.equals(type) && e.getEmployeeType() != EmployeeType.PART_TIME) {
                continue;
            }
            result.add(e);
        }

        visible.setAll(result);

        int total = AppContext.employees().getEmployeeCount();
        countLabel.setText("Showing " + result.size() + " of " + total
                + (total == 1 ? " employee" : " employees"));

        Label placeholder = new Label(total == 0
                ? "No employees yet. Click \"+ Add Employee\" to create the first one."
                : "No employees match your search.");
        placeholder.getStyleClass().add("placeholder-text");
        table.setPlaceholder(placeholder);
    }

    @FXML
    private void onClearFilters() {
        searchField.clear();
        departmentFilter.setValue(ALL_DEPARTMENTS);
        typeFilter.setValue(ALL_TYPES);
    }

    // ======================================================================
    // actions
    // ======================================================================

    @FXML
    private void onAddEmployee() {
        AppContext.setEditMode(false);
        AppContext.setSelectedEmployee(null);
        Navigator.goTo(Page.ADD_EMPLOYEE);
    }

    private void onView(Employee e) {
        if (e == null) {
            return;
        }
        AppContext.setSelectedEmployee(e);
        AppContext.setEditMode(false);
        Navigator.goTo(Page.EMPLOYEE_PROFILE);
    }

    private void onEdit(Employee e) {
        if (e == null) {
            return;
        }
        AppContext.setSelectedEmployee(e);
        AppContext.setEditMode(true);
        Navigator.goTo(Page.ADD_EMPLOYEE);
    }

    private void onDelete(Employee e) {
        if (e == null) {
            return;
        }
        hideMessage();

        ButtonType deleteType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(deleteType, cancelType);
        alert.setTitle("Delete employee");
        alert.setHeaderText("Delete " + e.getName() + "?");
        alert.setContentText("This permanently removes the employee and their profile picture. "
                + "This cannot be undone.");
        alert.setGraphic(null);
        alert.initOwner(table.getScene().getWindow());

        // Use the app's look for the dialog
        DialogPane pane = alert.getDialogPane();
        URL css = getClass().getResource("/com/ems/css/style.css");
        if (css != null) {
            pane.getStylesheets().add(css.toExternalForm());
        }
        pane.lookupButton(deleteType).getStyleClass().add("danger-button");
        pane.lookupButton(cancelType).getStyleClass().add("secondary-button");

        Optional<ButtonType> answer = alert.showAndWait();

        if (answer.isPresent() && answer.get() == deleteType) {
            boolean deleted = AppContext.employees().deleteEmployee(e.getId());
            refresh();

            if (deleted) {
                showMessage("Employee \"" + e.getName() + "\" was deleted.", "banner-success");
            } else {
                showMessage("Could not delete the employee. Make sure the data folder can be written to.",
                        "banner-error");
            }
        }
    }

    // ======================================================================
    // message banner
    // ======================================================================

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