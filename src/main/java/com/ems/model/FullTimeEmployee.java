package com.ems.model;

public class FullTimeEmployee extends Employee {

    private static final long serialVersionUID = 1L;
    private double basicSalary;
    private double bonus;

    public FullTimeEmployee(int id, String name, int age,
                            String department, String position,
                            double basicSalary, double bonus) {

        super(id, name, age, department, position);

        setBasicSalary(basicSalary);
        setBonus(bonus);
    }

    // Calculate salary without modifying the object
    @Override
    public double calculateSalary() {
        return basicSalary + bonus;
    }

    // Employee type
    @Override
    public EmployeeType getEmployeeType() {
        return EmployeeType.FULL_TIME;
    }

    // Getters

    public double getBasicSalary() {
        return basicSalary;
    }

    public double getBonus() {
        return bonus;
    }

    // Setters with validation

    public void setBasicSalary(double basicSalary) {

        if (basicSalary < 0 || Double.isNaN(basicSalary)
                || Double.isInfinite(basicSalary)) {

            throw new IllegalArgumentException(
                    "Basic salary must be a valid non-negative number.");
        }

        this.basicSalary = basicSalary;
    }

    public void setBonus(double bonus) {

        if (bonus < 0 || Double.isNaN(bonus)
                || Double.isInfinite(bonus)) {

            throw new IllegalArgumentException(
                    "Bonus must be a valid non-negative number.");
        }

        this.bonus = bonus;
    }
}
