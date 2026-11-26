package com.ems.model;

import java.time.LocalDate;

public class PartTimeEmployee extends Employee {

    private static final long serialVersionUID = 1L;

    private double hourlyRate;
    private double hoursWorked;

    public PartTimeEmployee(int id, String name, int age,
                            String department, String position,
                            String email, String phone, LocalDate hireDate,
                            double hourlyRate, double hoursWorked) {

        super(id, name, age, department, position, email, phone, hireDate);

        validateAmount(hourlyRate, "Hourly rate");
        validateAmount(hoursWorked, "Hours worked");

        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    // Calculate salary without modifying the object
    @Override
    public double calculateSalary() {
        return hourlyRate * hoursWorked;
    }

    @Override
    public EmployeeType getEmployeeType() {
        return EmployeeType.PART_TIME;
    }

    // Getters

    public double getHourlyRate() {
        return hourlyRate;
    }

    public double getHoursWorked() {
        return hoursWorked;
    }

    // Setters with validation

    public void setHourlyRate(double hourlyRate) {
        validateAmount(hourlyRate, "Hourly rate");
        this.hourlyRate = hourlyRate;
    }

    public void setHoursWorked(double hoursWorked) {
        validateAmount(hoursWorked, "Hours worked");
        this.hoursWorked = hoursWorked;
    }

    private static void validateAmount(double value, String label) {
        if (value < 0 || Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(
                    label + " must be a valid non-negative number.");
        }
    }
}
