package com.ems.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.ems.AppContext;
import com.ems.Navigator;
import com.ems.Navigator.Page;
import com.ems.io.FileHandler;
import com.ems.model.Employee;
import com.ems.model.EmployeeType;
import com.ems.model.FullTimeEmployee;
import com.ems.model.PartTimeEmployee;

import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

/** Add Employee page. The same form is reused for editing (AppContext.isEditMode()). */
public class AddEmployeeController {

    private static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024;

    // Profile picture
    @FXML private ImageView profileImage;
    @FXML private Label avatarInitial;
    @FXML private Button chooseImageButton, removeImageButton;

    // Personal information
    @FXML private TextField nameField, ageField, emailField, phoneField;

    // Job information
    @FXML private TextField employeeIdField, positionField;
    @FXML private ComboBox<String> departmentCombo;
    @FXML private ComboBox<EmployeeType> typeCombo;
    @FXML private DatePicker hireDatePicker;

    // Salary information
    @FXML private Node fullTimeGrid, partTimeGrid;
    @FXML private TextField salaryField, hourlyRateField, hoursField;
    @FXML private Label salaryPreview;

    // Messages and buttons
    @FXML private Label formMessage;
    @FXML private Button submitButton;

    private final FileHandler fileHandler = new FileHandler();

    private Employee editing;          // null = adding a new employee
    private Path newImageSource;       // picture chosen in this session, not copied yet
    private boolean imageRemoved;      // the user pressed "Remove"
    private String currentImagePath;   // stored picture of the employee being edited

    private List<Control> inputs;

    // ======================================================================
    // setup
    // ======================================================================

    @FXML
    private void initialize() {
        editing = AppContext.isEditMode() ? AppContext.getSelectedEmployee() : null;

        inputs = List.of(nameField, ageField, emailField, phoneField, positionField,
                departmentCombo, hireDatePicker, typeCombo,
                salaryField, hourlyRateField, hoursField);

        // Only allow sensible characters while typing
        restrict(ageField, "\\d{0,3}");
        restrict(phoneField, "[0-9+()\\- ]{0,20}");
        restrict(salaryField, "\\d{0,9}(\\.\\d{0,2})?");
        restrict(hourlyRateField, "\\d{0,9}(\\.\\d{0,2})?");
        restrict(hoursField, "\\d{0,4}(\\.\\d{0,2})?");

        departmentCombo.getItems().setAll(AppContext.departments().getNames());

        typeCombo.getItems().setAll(EmployeeType.values());
        typeCombo.setConverter(new StringConverter<EmployeeType>() {
            @Override
            public String toString(EmployeeType type) {
                if (type == null) {
                    return "";
                }
                return (type == EmployeeType.FULL_TIME) ? "Full-Time" : "Part-Time";
            }

            @Override
            public EmployeeType fromString(String text) {
                return null;   // the combo box is not editable
            }
        });

        hireDatePicker.setEditable(false);

        // Remove the red border as soon as the user fixes a field
        for (Control c : inputs) {
            if (c instanceof TextField) {
                ((TextField) c).textProperty().addListener((obs, o, n) -> c.getStyleClass().remove("invalid"));
            } else if (c instanceof ComboBox) {
                ((ComboBox<?>) c).valueProperty().addListener((obs, o, n) -> c.getStyleClass().remove("invalid"));
            } else if (c instanceof DatePicker) {
                ((DatePicker) c).valueProperty().addListener((obs, o, n) -> c.getStyleClass().remove("invalid"));
            }
        }

        // Live updates
        typeCombo.valueProperty().addListener((obs, o, n) -> updateSalarySection());
        salaryField.textProperty().addListener((obs, o, n) -> updateSalaryPreview());
        hourlyRateField.textProperty().addListener((obs, o, n) -> updateSalaryPreview());
        hoursField.textProperty().addListener((obs, o, n) -> updateSalaryPreview());
        nameField.textProperty().addListener((obs, o, n) -> updateAvatarInitial());

        // Round profile picture
        profileImage.setClip(new Circle(70, 70, 70));

        if (editing != null) {
            submitButton.setText("Save Changes");
            loadEmployee(editing);
        } else {
            resetForm();
        }
    }

    // Blocks any typed/pasted text that does not match the pattern
    private void restrict(TextField field, String regex) {
        field.setTextFormatter(new TextFormatter<String>(
                change -> change.getControlNewText().matches(regex) ? change : null));
    }

    // ======================================================================
    // filling the form
    // ======================================================================

    private void resetForm() {
        nameField.clear();
        ageField.clear();
        emailField.clear();
        phoneField.clear();
        positionField.clear();
        salaryField.clear();
        hourlyRateField.clear();
        hoursField.clear();

        employeeIdField.setText(String.valueOf(AppContext.employees().getNextId()));
        departmentCombo.setValue(null);
        hireDatePicker.setValue(LocalDate.now());
        typeCombo.setValue(EmployeeType.FULL_TIME);

        newImageSource = null;
        imageRemoved = false;
        currentImagePath = null;
        refreshImage();

        clearValidation();
        clearMessage();
        updateSalarySection();
        updateAvatarInitial();
    }

    // Edit mode: fill every field from an existing employee (also used by "Reset Form")
    private void loadEmployee(Employee e) {
        nameField.setText(e.getName());
        ageField.setText(String.valueOf(e.getAge()));
        emailField.setText(e.getEmail());
        phoneField.setText(e.getPhone());
        positionField.setText(e.getPosition());
        employeeIdField.setText(String.valueOf(e.getId()));
        hireDatePicker.setValue(e.getHireDate());

        selectDepartment(e.getDepartment());

        salaryField.clear();
        hourlyRateField.clear();
        hoursField.clear();

        if (e instanceof FullTimeEmployee) {
            typeCombo.setValue(EmployeeType.FULL_TIME);
            salaryField.setText(format(((FullTimeEmployee) e).getBasicSalary()));
        } else if (e instanceof PartTimeEmployee) {
            typeCombo.setValue(EmployeeType.PART_TIME);
            hourlyRateField.setText(format(((PartTimeEmployee) e).getHourlyRate()));
            hoursField.setText(format(((PartTimeEmployee) e).getHoursWorked()));
        }

        newImageSource = null;
        imageRemoved = false;
        currentImagePath = e.getImagePath();
        refreshImage();

        clearValidation();
        clearMessage();
        updateSalarySection();
        updateAvatarInitial();
    }

    // Selects the employee's department; adds it to the list if it was deleted/renamed meanwhile
    private void selectDepartment(String name) {
        for (String item : departmentCombo.getItems()) {
            if (item.equalsIgnoreCase(name)) {
                departmentCombo.setValue(item);
                return;
            }
        }
        departmentCombo.getItems().add(name);
        departmentCombo.setValue(name);
    }

    private static String format(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros().toPlainString();
    }

    // ======================================================================
    // salary section
    // ======================================================================

    private void updateSalarySection() {
        boolean fullTime = typeCombo.getValue() != EmployeeType.PART_TIME;

        fullTimeGrid.setVisible(fullTime);
        fullTimeGrid.setManaged(fullTime);
        partTimeGrid.setVisible(!fullTime);
        partTimeGrid.setManaged(!fullTime);

        updateSalaryPreview();
    }

    private void updateSalaryPreview() {
        double value;
        if (typeCombo.getValue() == EmployeeType.PART_TIME) {
            value = parseOrZero(hourlyRateField.getText()) * parseOrZero(hoursField.getText());
        } else {
            value = parseOrZero(salaryField.getText()) + preservedBonus();
        }
        salaryPreview.setText(String.format("Calculated salary: %,.2f", value));
    }

    private static double parseOrZero(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException | NullPointerException ex) {
            return 0;
        }
    }

    // The form has no bonus field, so an existing bonus is kept when editing
    private double preservedBonus() {
        return (editing instanceof FullTimeEmployee) ? ((FullTimeEmployee) editing).getBonus() : 0;
    }

    // ======================================================================
    // profile picture
    // ======================================================================

    @FXML
    private void onChooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose profile picture");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));

        File file = chooser.showOpenDialog(chooseImageButton.getScene().getWindow());
        if (file == null) {
            return;
        }

        if (file.length() > MAX_IMAGE_BYTES) {
            showError("The picture is larger than 5 MB. Please choose a smaller one.");
            return;
        }

        if (new Image(file.toURI().toString()).isError()) {
            showError("That file could not be read as an image.");
            return;
        }

        clearMessage();
        newImageSource = file.toPath();
        imageRemoved = false;
        refreshImage();
    }

    @FXML
    private void onRemoveImage() {
        newImageSource = null;
        imageRemoved = true;
        refreshImage();
    }

    // Shows the right picture (new one, stored one, or the initial letter)
    private void refreshImage() {
        Path shown = null;
        if (newImageSource != null) {
            shown = newImageSource;
        } else if (!imageRemoved && currentImagePath != null) {
            shown = Paths.get(currentImagePath);
        }

        boolean displayed = false;
        if (shown != null && Files.exists(shown)) {
            Image image = new Image(shown.toUri().toString());
            if (!image.isError()) {
                // Crop to a centered square so the circle is always filled
                double side = Math.min(image.getWidth(), image.getHeight());
                profileImage.setViewport(new Rectangle2D(
                        (image.getWidth() - side) / 2, (image.getHeight() - side) / 2, side, side));
                profileImage.setImage(image);
                displayed = true;
            }
        }

        if (!displayed) {
            profileImage.setImage(null);
        }
        profileImage.setVisible(displayed);
        avatarInitial.setVisible(!displayed);
        removeImageButton.setDisable(shown == null);
    }

    private void updateAvatarInitial() {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        avatarInitial.setText(name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase());
    }

    // ======================================================================
    // buttons
    // ======================================================================

    @FXML
    private void onReset() {
        if (editing != null) {
            loadEmployee(editing);     // back to the saved values
        } else {
            resetForm();
        }
    }

    @FXML
    private void onCancel() {
        AppContext.setEditMode(false);
        Navigator.goTo(editing != null ? Page.EMPLOYEE_PROFILE : Page.EMPLOYEE_LIST);
    }

    @FXML
    private void onSubmit() {
        clearValidation();
        clearMessage();

        // ---- 1. required fields ----
        boolean missing = false;
        missing |= markIfBlank(nameField);
        missing |= markIfBlank(ageField);
        missing |= markIfBlank(emailField);
        missing |= markIfBlank(phoneField);
        missing |= markIfBlank(positionField);
        missing |= markIfEmpty(departmentCombo, departmentCombo.getValue() == null);
        missing |= markIfEmpty(hireDatePicker, hireDatePicker.getValue() == null);
        missing |= markIfEmpty(typeCombo, typeCombo.getValue() == null);

        EmployeeType type = typeCombo.getValue();
        if (type == EmployeeType.PART_TIME) {
            missing |= markIfBlank(hourlyRateField);
            missing |= markIfBlank(hoursField);
        } else if (type == EmployeeType.FULL_TIME) {
            missing |= markIfBlank(salaryField);
        }

        if (missing) {
            showError("Please fill in all the highlighted fields.");
            return;
        }

        // ---- 2. build the employee (the model checks email, phone, age range...) ----
        int id = (editing != null) ? editing.getId() : AppContext.employees().getNextId();
        Employee employee;

        try {
            String name = nameField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String department = departmentCombo.getValue();
            String position = positionField.getText().trim();
            LocalDate hireDate = hireDatePicker.getValue();

            if (type == EmployeeType.FULL_TIME) {
                double salary = Double.parseDouble(salaryField.getText().trim());
                employee = new FullTimeEmployee(id, name, age, department, position,
                        email, phone, hireDate, salary, preservedBonus());
            } else {
                double rate = Double.parseDouble(hourlyRateField.getText().trim());
                double hours = Double.parseDouble(hoursField.getText().trim());
                employee = new PartTimeEmployee(id, name, age, department, position,
                        email, phone, hireDate, rate, hours);
            }
        } catch (NumberFormatException ex) {
            showError("Please enter valid numbers in the age and salary fields.");
            return;
        } catch (IllegalArgumentException ex) {
            markByMessage(ex.getMessage());
            showError(ex.getMessage());
            return;
        }

        // ---- 3. email must be unique ----
        if (AppContext.employees().isEmailTaken(employee.getEmail(), id)) {
            mark(emailField);
            showError("Another employee already uses this email address.");
            return;
        }

        // ---- 4. profile picture ----
        String oldPath = (editing != null) ? editing.getImagePath() : null;
        String newPath = oldPath;

        try {
            if (newImageSource != null) {
                newPath = fileHandler.saveProfileImage(newImageSource, id);
            } else if (imageRemoved) {
                newPath = null;
            }
        } catch (IOException ex) {
            showError("Could not save the picture: " + ex.getMessage());
            return;
        }
        employee.setImagePath(newPath);

        // ---- 5. save ----
        boolean saved;
        if (editing == null) {
            saved = AppContext.employees().addEmployee(employee);
        } else {
            saved = AppContext.employees().updateEmployee(editing.getId(), employee) != null;
        }

        if (!saved) {
            if (newImageSource != null && newPath != null && !newPath.equals(oldPath)) {
                fileHandler.deleteProfileImage(newPath);    // don't leave an unused picture behind
            }
            showError("Could not save the employee. Make sure the data folder can be written to, then try again.");
            return;
        }

        if (oldPath != null && !oldPath.equals(newPath)) {
            fileHandler.deleteProfileImage(oldPath);        // old picture is no longer used
        }

        // ---- 6. done ----
        if (editing == null) {
            String savedName = employee.getName();
            resetForm();
            showSuccess("Employee \"" + savedName + "\" was added successfully.");
        } else {
            AppContext.setSelectedEmployee(employee);
            AppContext.setEditMode(false);
            Navigator.goTo(Page.EMPLOYEE_PROFILE);
        }
    }

    // ======================================================================
    // validation helpers
    // ======================================================================

    private boolean markIfBlank(TextField field) {
        boolean blank = field.getText() == null || field.getText().trim().isEmpty();
        return markIfEmpty(field, blank);
    }

    private boolean markIfEmpty(Control control, boolean empty) {
        if (empty) {
            mark(control);
        }
        return empty;
    }

    private void mark(Control control) {
        if (!control.getStyleClass().contains("invalid")) {
            control.getStyleClass().add("invalid");
        }
    }

    // The model's messages start with the field name, so use them to highlight the right field
    private void markByMessage(String message) {
        if (message == null) {
            return;
        }
        String m = message.toLowerCase();
        if (m.contains("email")) {
            mark(emailField);
        } else if (m.contains("phone")) {
            mark(phoneField);
        } else if (m.contains("age")) {
            mark(ageField);
        } else if (m.contains("name")) {
            mark(nameField);
        } else if (m.contains("position")) {
            mark(positionField);
        } else if (m.contains("hourly")) {
            mark(hourlyRateField);
        } else if (m.contains("hours")) {
            mark(hoursField);
        } else if (m.contains("salary") || m.contains("bonus")) {
            mark(salaryField);
        }
    }

    private void clearValidation() {
        for (Control c : inputs) {
            c.getStyleClass().remove("invalid");
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
        formMessage.getStyleClass().removeAll("banner-error", "banner-success");
        formMessage.getStyleClass().add(styleClass);
        formMessage.setText(text);
        formMessage.setVisible(true);
        formMessage.setManaged(true);
    }

    private void clearMessage() {
        formMessage.setText("");
        formMessage.setVisible(false);
        formMessage.setManaged(false);
    }
}