package com.ems.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ems.AppContext;
import com.ems.Avatars;
import com.ems.Navigator;
import com.ems.Navigator.Page;
import com.ems.manager.EmployeeManager;
import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.EmployeeType;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/** Dashboard: summary numbers, employees per department, and the most recently added employees. */
public class DashboardController {

    private static final int RECENT_COUNT = 5;
    private static final String BULLET = "\u2022";

    @FXML private Label dateLabel;
    @FXML private Label totalValue, totalSub;
    @FXML private Label fullTimeValue, fullTimeSub;
    @FXML private Label partTimeValue, partTimeSub;
    @FXML private Label payrollValue, payrollSub;
    @FXML private VBox departmentRows, recentRows;

    @FXML
    private void initialize() {
        dateLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH)));

        fillSummary();
        fillDepartments();
        fillRecent();
    }

    // ======================================================================
    // summary cards
    // ======================================================================

    private void fillSummary() {
        EmployeeManager employees = AppContext.employees();

        int total = employees.getEmployeeCount();
        int fullTime = employees.countByType(EmployeeType.FULL_TIME);
        int partTime = employees.countByType(EmployeeType.PART_TIME);
        int departments = AppContext.departments().getAllDepartments().size();

        totalValue.setText(String.valueOf(total));
        totalSub.setText("across " + departments + (departments == 1 ? " department" : " departments"));

        fullTimeValue.setText(String.valueOf(fullTime));
        fullTimeSub.setText(percent(fullTime, total) + "% of all employees");

        partTimeValue.setText(String.valueOf(partTime));
        partTimeSub.setText(percent(partTime, total) + "% of all employees");

        payrollValue.setText(String.format("%,.2f", employees.getTotalPayroll()));
        payrollSub.setText("Average: " + String.format("%,.2f", employees.getAverageSalary()) + " per employee");
    }

    private static long percent(int part, int total) {
        return (total == 0) ? 0 : Math.round(part * 100.0 / total);
    }

    // ======================================================================
    // employees per department
    // ======================================================================

    private void fillDepartments() {
        departmentRows.getChildren().clear();

        EmployeeManager employees = AppContext.employees();

        // department name -> number of employees
        Map<String, Integer> counts = new LinkedHashMap<>();
        int counted = 0;

        for (Department d : AppContext.departments().getAllDepartments()) {
            int n = employees.countInDepartment(d.getName());
            counts.put(d.getName(), n);
            counted += n;
        }

        // Employees whose department no longer exists (e.g. it was deleted) still get counted
        int others = employees.getEmployeeCount() - counted;
        if (others > 0) {
            counts.put("Other", others);
        }

        if (counts.isEmpty()) {
            departmentRows.getChildren().add(emptyLabel("No departments yet."));
            return;
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> {
            int diff = Integer.compare(b.getValue(), a.getValue());
            return diff != 0 ? diff : a.getKey().compareToIgnoreCase(b.getKey());
        });

        int max = entries.get(0).getValue();

        for (Map.Entry<String, Integer> entry : entries) {
            int count = entry.getValue();
            double ratio = (max == 0) ? 0 : (double) count / max;

            Label name = new Label(entry.getKey());
            name.getStyleClass().add("dept-name");
            Label number = new Label(count + (count == 1 ? " employee" : " employees"));
            number.getStyleClass().add("dept-count");

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            HBox top = new HBox(8, name, spacer, number);

            departmentRows.getChildren().add(new VBox(6, top, buildBar(ratio)));
        }
    }

    // A thin bar whose filled part is "ratio" of the full width
    private Node buildBar(double ratio) {
        StackPane track = new StackPane();
        track.getStyleClass().add("bar-track");
        track.setMinHeight(10);
        track.setPrefHeight(10);
        track.setMaxHeight(10);

        Region fill = new Region();
        fill.getStyleClass().add("bar-fill");
        fill.setMaxWidth(Region.USE_PREF_SIZE);
        fill.prefWidthProperty().bind(track.widthProperty().multiply(ratio));
        StackPane.setAlignment(fill, Pos.CENTER_LEFT);

        track.getChildren().add(fill);
        return track;
    }

    // ======================================================================
    // recently added
    // ======================================================================

    private void fillRecent() {
        recentRows.getChildren().clear();

        List<Employee> all = AppContext.employees().getAllEmployees();

        if (all.isEmpty()) {
            recentRows.getChildren().add(emptyLabel("No employees yet. Add the first one!"));
            return;
        }

        // New employees get the highest ID, so the newest are the ones with the largest IDs
        all.sort((a, b) -> Integer.compare(b.getId(), a.getId()));

        int shown = Math.min(RECENT_COUNT, all.size());
        for (int i = 0; i < shown; i++) {
            recentRows.getChildren().add(buildRecentRow(all.get(i)));
        }
    }

    private Node buildRecentRow(Employee e) {
        Label name = new Label(e.getName());
        name.getStyleClass().add("recent-name");
        Label sub = new Label(e.getPosition() + "  " + BULLET + "  " + e.getDepartment());
        sub.getStyleClass().add("recent-sub");

        boolean fullTime = e.getEmployeeType() == EmployeeType.FULL_TIME;
        Label badge = new Label(fullTime ? "Full-Time" : "Part-Time");
        badge.getStyleClass().addAll("badge", fullTime ? "badge-fulltime" : "badge-parttime");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(12, Avatars.create(e, 40), new VBox(2, name, sub), spacer, badge);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("recent-row");
        row.setOnMouseClicked(event -> openProfile(e));
        return row;
    }

    private Label emptyLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("placeholder-text");
        return label;
    }

    // ======================================================================
    // buttons
    // ======================================================================

    private void openProfile(Employee e) {
        AppContext.setSelectedEmployee(e);
        AppContext.setEditMode(false);
        Navigator.goTo(Page.EMPLOYEE_PROFILE);
    }

    @FXML
    private void onAddEmployee() {
        AppContext.setEditMode(false);
        AppContext.setSelectedEmployee(null);
        Navigator.goTo(Page.ADD_EMPLOYEE);
    }

    @FXML
    private void onViewAll() {
        Navigator.goTo(Page.EMPLOYEE_LIST);
    }
}