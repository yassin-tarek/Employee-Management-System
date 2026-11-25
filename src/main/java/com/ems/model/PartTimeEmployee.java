package com.ems.model;

public class PartTimeEmployee extends Employee {

    private static final long serialVersionUID = 1L;
    private double hourlyRate;
    private double hoursWorked;

    public PartTimeEmployee(int id, String name, int age,
                            String department, String position,
                            double hourlyRate, double hoursWorked) {

        super(id, name, age, department, position);

        setHourlyRate(hourlyRate);
        setHoursWorked(hoursWorked);
    }

    // Calculate salary without modifying the object
    @Override
    public double calculateSalary() {
        return hourlyRate * hoursWorked;
    }

    // Employee type
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

        if (hourlyRate < 0 || Double.isNaN(hourlyRate)
                || Double.isInfinite(hourlyRate)) {

            throw new IllegalArgumentException(
                    "Hourly rate must be a valid non-negative number.");
        }

        this.hourlyRate = hourlyRate;
    }

    public void setHoursWorked(double hoursWorked) {

        if (hoursWorked < 0 || Double.isNaN(hoursWorked)
                || Double.isInfinite(hoursWorked)) {

            throw new IllegalArgumentException(
                    "Hours worked must be a valid non-negative number.");
        }

        this.hoursWorked = hoursWorked;
    }
}
