package com.ems.model;
public class PartTimeEmployee extends Employee {

    private double hourlyRate;
    private double hoursWorked;
    public PartTimeEmployee(int id, String name, int age,
                            String department, String position,
                            double hourlyRate, double hoursWorked) {

        super(id, name, age, department, position, 0.0);

        this.hourlyRate = hourlyRate;
        this.hoursWorked = hoursWorked;
    }

    @Override
    public double calculateSalary() {
        double salary = hourlyRate * hoursWorked;
        setSalary(salary);
        return salary;
    }
    public double getHourlyRate() {
        return hourlyRate;
    }
    public double getHoursWorked() {
        return hoursWorked;
    }
    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    public void setHoursWorked(double hoursWorked) {
        this.hoursWorked = hoursWorked;
    }
}
