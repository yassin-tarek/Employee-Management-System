package com.ems.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.regex.Pattern;

public abstract class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int MIN_AGE = 16;
    public static final int MAX_AGE = 100;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+?[0-9 ()-]{7,20}$");

    private final int id;
    private String name;
    private int age;
    private String department;
    private String position;
    private String email;
    private String phone;
    private LocalDate hireDate;
    private String imagePath;

    // Constructor
    public Employee(int id, String name, int age,
                    String department, String position,
                    String email, String phone, LocalDate hireDate) {

        validateId(id);
        validateName(name);
        validateAge(age);
        validateDepartment(department);
        validatePosition(position);
        validateEmail(email);
        validatePhone(phone);
        validateHireDate(hireDate);

        this.id = id;
        this.name = name.trim();
        this.age = age;
        this.department = department.trim();
        this.position = position.trim();
        this.email = email.trim().toLowerCase();
        this.phone = phone.trim();
        this.hireDate = hireDate;
    }

    // Abstract methods
    public abstract double calculateSalary();

    public abstract EmployeeType getEmployeeType();

    // Validation methods (static: they don't depend on object state)

    private static void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be a positive integer.");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }
    }

    private static void validateAge(int age) {
        if (age < MIN_AGE || age > MAX_AGE) {
            throw new IllegalArgumentException(
                    "Age must be between " + MIN_AGE + " and " + MAX_AGE + ".");
        }
    }

    private static void validateDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department cannot be null or empty.");
        }
    }

    private static void validatePosition(String position) {
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException("Position cannot be null or empty.");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Email address is not valid.");
        }
    }

    private static void validatePhone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone.trim()).matches()) {
            throw new IllegalArgumentException("Phone number is not valid.");
        }
    }

    private static void validateHireDate(LocalDate hireDate) {
        if (hireDate == null) {
            throw new IllegalArgumentException("Hire date cannot be null.");
        }
    }

    // Getters

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDepartment() {
        return department;
    }

    public String getPosition() {
        return position;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public String getImagePath() {
        return imagePath;
    }

    // Setters

    public void setName(String name) {
        validateName(name);
        this.name = name.trim();
    }

    public void setAge(int age) {
        validateAge(age);
        this.age = age;
    }

    public void setDepartment(String department) {
        validateDepartment(department);
        this.department = department.trim();
    }

    public void setPosition(String position) {
        validatePosition(position);
        this.position = position.trim();
    }

    public void setEmail(String email) {
        validateEmail(email);
        this.email = email.trim().toLowerCase();
    }

    public void setPhone(String phone) {
        validatePhone(phone);
        this.phone = phone.trim();
    }

    public void setHireDate(LocalDate hireDate) {
        validateHireDate(hireDate);
        this.hireDate = hireDate;
    }

    // null or blank means "no profile picture"
    public void setImagePath(String imagePath) {
        this.imagePath = (imagePath == null || imagePath.trim().isEmpty())
                ? null : imagePath.trim();
    }

    // Two employees are the same if they have the same ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        return id == ((Employee) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}