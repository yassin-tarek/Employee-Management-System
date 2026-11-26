package com.ems.model;

import java.time.LocalDate;

public class FullTimeEmployee extends Employee {

    private static final long serialVersionUID = 1L;

    private double basicSalary;   // monthly salary
    private double bonus;

    public FullTimeEmployee(int id, String name, int age,
                            String department, String position,
                            String email, String phone, LocalDate hireDate,
                            double basicSalary, double bonus) {

        super(id, name, age, department, position, email, phone, hireDate);

        validateAmount(basicSalary, "Basic salary");
        validateAmount(bonus, "Bonus");

        this.basicSalary = basicSalary;
        this.bonus = bonus;
    }

    // Convenience constructor: the Add Employee form has no bonus field
    public FullTimeEmployee(int id, String name, int age,
                            String department, String position,
                            String email, String phone, LocalDate hireDate,
                            double basicSalary) {

        this(id, name, age, department, position, email, phone, hireDate, basicSalary, 0);
    }

    // Calculate salary without modifying the object
    @Override
    public double calculateSalary() {
        return basicSalary + bonus;
    }

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
        validateAmount(basicSalary, "Basic salary");
        this.basicSalary = basicSalary;
    }

    public void setBonus(double bonus) {
        validateAmount(bonus, "Bonus");
        this.bonus = bonus;
    }

    private static void validateAmount(double value, String label) {
        if (value < 0 || Double.isNaN(value) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(
                    label + " must be a valid non-negative number.");
        }
    }
}