package com.ems.controller;

import java.util.List;

import com.ems.AppContext;
import com.ems.Navigator;
import com.ems.Navigator.Page;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/** Controller of the main window: sidebar + header + the content area that pages are loaded into. */
public class ShellController {

    private static final String ACTIVE = "active";

    @FXML
    private StackPane contentArea;

    @FXML
    private Label pageTitle;

    @FXML
    private Button dashboardNav, addEmployeeNav, employeeListNav, departmentsNav;

    private List<Button> navButtons;

    @FXML
    private void initialize() {
        navButtons = List.of(dashboardNav, addEmployeeNav, employeeListNav, departmentsNav);
    }

    // Called by Navigator every time a page is opened
    public void setContent(Node content, Page page) {
        boolean editing = (page == Page.ADD_EMPLOYEE) && AppContext.isEditMode();

        contentArea.getChildren().setAll(content);
        pageTitle.setText(editing ? "Edit Employee" : page.getTitle());
        highlight(navFor(page, editing));
    }

    private Button navFor(Page page, boolean editing) {
        switch (page) {
            case ADD_EMPLOYEE:
                // The Edit form is reached from the employee list, so highlight "Employees"
                return editing ? employeeListNav : addEmployeeNav;
            case EMPLOYEE_LIST:
            case EMPLOYEE_PROFILE:
                return employeeListNav;
            case DEPARTMENTS:
                return departmentsNav;
            case DASHBOARD:
            default:
                return dashboardNav;
        }
    }

    private void highlight(Button active) {
        for (Button b : navButtons) {
            b.getStyleClass().remove(ACTIVE);
        }
        active.getStyleClass().add(ACTIVE);
    }

    // ---------- sidebar actions ----------

    @FXML
    private void showDashboard() {
        Navigator.goTo(Page.DASHBOARD);
    }

    @FXML
    private void showAddEmployee() {
        // Sidebar "Add Employee" always opens an empty form (not edit mode)
        AppContext.setEditMode(false);
        AppContext.setSelectedEmployee(null);
        Navigator.goTo(Page.ADD_EMPLOYEE);
    }

    @FXML
    private void showEmployeeList() {
        Navigator.goTo(Page.EMPLOYEE_LIST);
    }

    @FXML
    private void showDepartments() {
        Navigator.goTo(Page.DEPARTMENTS);
    }

    @FXML
    private void logout() {
        AppContext.setEditMode(false);
        AppContext.setSelectedEmployee(null);
        Navigator.showLogin();
    }
}