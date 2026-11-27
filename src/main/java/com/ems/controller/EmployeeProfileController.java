package com.ems.controller;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.ems.AppContext;
import com.ems.Avatars;
import com.ems.Dialogs;
import com.ems.Navigator;
import com.ems.Navigator.Page;
import com.ems.model.Employee;
import com.ems.model.EmployeeType;
import com.ems.model.FullTimeEmployee;
import com.ems.model.PartTimeEmployee;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Shows everything about the employee chosen in the Employees list (AppContext.getSelectedEmployee()). */
public class EmployeeProfileController {

    private static final String DASH = "\u2014";      // em dash
    private static final String BULLET = "\u2022";    // bullet

    @FXML private VBox profileContent;
    @FXML private Label emptyState, statusMessage;

    @FXML private StackPane avatarHolder;
    @FXML private Label nameLabel, subtitleLabel, typeBadge;

    @FXML private Label emailValue, phoneValue, ageValue, idValue;
    @FXML private Label departmentValue, positionValue, hireDateValue, tenureValue, typeValue;

    @FXML private VBox salaryRows;

    private Employee employee;

    // ======================================================================
    // setup
    // ======================================================================

    @FXML
    private void initialize() {
        hideMessage();

        // Read the employee again from the manager so we always show the saved data
        Employee selected = AppContext.getSelectedEmployee();
        employee = (selected == null) ? null : AppContext.employees().searchEmployee(selected.getId());

        if (employee == null) {
            profileContent.setVisible(false);
            profileContent.setManaged(false);
            return;
        }

        emptyState.setVisible(false);
        emptyState.setManaged(false);

        AppContext.setSelectedEmployee(employee);
        fill();
    }

    private void fill() {
        avatarHolder.getChildren().setAll(Avatars.create(employee, 110));

        nameLabel.setText(employee.getName());
        subtitleLabel.setText(employee.getPosition() + "  " + BULLET + "  " + employee.getDepartment());

        boolean fullTime = employee.getEmployeeType() == EmployeeType.FULL_TIME;
        String typeText = fullTime ? "Full-Time" : "Part-Time";

        typeBadge.setText(typeText);
        typeBadge.getStyleClass().removeAll("badge-fulltime", "badge-parttime");
        typeBadge.getStyleClass().add(fullTime ? "badge-fulltime" : "badge-parttime");

        emailValue.setText(valueOrDash(employee.getEmail()));
        phoneValue.setText(valueOrDash(employee.getPhone()));
        ageValue.setText(employee.getAge() + " years");
        idValue.setText(String.valueOf(employee.getId()));

        departmentValue.setText(valueOrDash(employee.getDepartment()));
        positionValue.setText(valueOrDash(employee.getPosition()));
        typeValue.setText(typeText);

        LocalDate hired = employee.getHireDate();
        hireDateValue.setText(hired == null
                ? DASH
                : hired.format(DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH)));
        tenureValue.setText(tenure(hired));

        fillSalary();
    }

    private void fillSalary() {
        salaryRows.getChildren().clear();

        if (employee instanceof FullTimeEmployee) {
            FullTimeEmployee full = (FullTimeEmployee) employee;
            addRow("Monthly Salary", money(full.getBasicSalary()));
            addRow("Bonus", money(full.getBonus()));
        } else if (employee instanceof PartTimeEmployee) {
            PartTimeEmployee part = (PartTimeEmployee) employee;
            addRow("Hourly Rate", money(part.getHourlyRate()));
            addRow("Hours Worked", new DecimalFormat("#,##0.##").format(part.getHoursWorked()));
        }

        Region line = new Region();
        line.getStyleClass().add("divider");
        line.setMinHeight(1);
        line.setPrefHeight(1);
        line.setMaxHeight(1);
        salaryRows.getChildren().add(line);

        Label totalLabel = new Label("Total Salary");
        totalLabel.getStyleClass().add("salary-total-label");
        Label totalValue = new Label(money(employee.calculateSalary()));
        totalValue.getStyleClass().add("salary-total-value");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox total = new HBox(8, totalLabel, spacer, totalValue);
        total.getStyleClass().add("salary-total");
        salaryRows.getChildren().add(total);
    }

    private void addRow(String label, String value) {
        Label name = new Label(label);
        name.getStyleClass().add("salary-label");
        Label amount = new Label(value);
        amount.getStyleClass().add("salary-value");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        salaryRows.getChildren().add(new HBox(8, name, spacer, amount));
    }

    // ======================================================================
    // formatting helpers
    // ======================================================================

    private static String money(double value) {
        return String.format("%,.2f", value);
    }

    private static String valueOrDash(String text) {
        return (text == null || text.trim().isEmpty()) ? DASH : text;
    }

    // "2 years, 3 months", "5 months", "Started today", ...
    private static String tenure(LocalDate hired) {
        if (hired == null) {
            return DASH;
        }

        LocalDate today = LocalDate.now();
        if (hired.isAfter(today)) {
            return "Has not started yet";
        }

        Period period = Period.between(hired, today);
        List<String> parts = new ArrayList<>();

        if (period.getYears() > 0) {
            parts.add(period.getYears() + (period.getYears() == 1 ? " year" : " years"));
        }
        if (period.getMonths() > 0) {
            parts.add(period.getMonths() + (period.getMonths() == 1 ? " month" : " months"));
        }
        if (parts.isEmpty()) {
            if (period.getDays() == 0) {
                return "Started today";
            }
            parts.add(period.getDays() + (period.getDays() == 1 ? " day" : " days"));
        }

        return String.join(", ", parts);
    }

    // ======================================================================
    // buttons
    // ======================================================================

    @FXML
    private void onBack() {
        Navigator.goTo(Page.EMPLOYEE_LIST);
    }

    @FXML
    private void onEdit() {
        if (employee == null) {
            return;
        }
        AppContext.setSelectedEmployee(employee);
        AppContext.setEditMode(true);
        Navigator.goTo(Page.ADD_EMPLOYEE);
    }

    @FXML
    private void onDelete() {
        if (employee == null) {
            return;
        }
        hideMessage();

        boolean confirmed = Dialogs.confirmDelete(
                nameLabel.getScene().getWindow(),
                "Delete " + employee.getName() + "?",
                "This permanently removes the employee and their profile picture. "
                        + "This cannot be undone.");

        if (!confirmed) {
            return;
        }

        if (AppContext.employees().deleteEmployee(employee.getId())) {
            AppContext.setSelectedEmployee(null);
            Navigator.goTo(Page.EMPLOYEE_LIST);
        } else {
            showError("Could not delete the employee. Make sure the data folder can be written to.");
        }
    }

    // ======================================================================
    // message banner
    // ======================================================================

    private void showError(String text) {
        statusMessage.getStyleClass().removeAll("banner-error", "banner-success");
        statusMessage.getStyleClass().add("banner-error");
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