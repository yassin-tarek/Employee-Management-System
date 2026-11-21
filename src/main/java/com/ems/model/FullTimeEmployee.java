package com.ems.model;
public class FullTimeEmployee extends Employee {

    private double basicSalary;
    private double bonus;

    public FullTimeEmployee(int id, String name, int age,
                            String department, String position,
                            double basicSalary, double bonus) {

        super(id, name, age, department, position, 0.0);

        this.basicSalary = basicSalary;
        this.bonus = bonus;
    }

    @Override
    public double calculateSalary() {
        double salary = basicSalary + bonus;
        setSalary(salary);
        return salary;
    }
}
