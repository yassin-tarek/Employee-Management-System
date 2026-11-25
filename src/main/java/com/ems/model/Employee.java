package com.ems.model;

import java.io.Serializable;

public abstract class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String name;
    private int age;
    private String department;
    private String position;

    // Constructor
    public Employee(int id, String name, int age,
                    String department, String position) {

        validateId(id);
        validateName(name);
        validateAge(age);
        validateDepartment(department);
        validatePosition(position);

        this.id = id;
        this.name = name.trim();
        this.age = age;
        this.department = department.trim();
        this.position = position.trim();
    }

    // Abstract methods
    public abstract double calculateSalary();

    public abstract EmployeeType getEmployeeType();

    // Validation methods

    private void validateId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException(
                    "ID must be a positive integer.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Name cannot be null or empty.");
        }
    }

    private void validateAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException(
                    "Age must be a positive integer.");
        }
    }

    private void validateDepartment(String department) {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Department cannot be null or empty.");
        }
    }

    private void validatePosition(String position) {
        if (position == null || position.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Position cannot be null or empty.");
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
}
