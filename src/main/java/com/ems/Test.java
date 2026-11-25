package com.ems;

import com.ems.model.Employee;
import com.ems.model.FullTimeEmployee;
import com.ems.model.PartTimeEmployee;
import com.ems.model.EmployeeType;

public class Test {

    public static void main(String[] args) {

        System.out.println("===== FULL-TIME EMPLOYEE TEST =====");

        FullTimeEmployee fullTime = new FullTimeEmployee(
                1,
                "Ahmed",
                25,
                "IT",
                "Software Developer",
                1200,
                300
        );

        System.out.println("ID: " + fullTime.getId());
        System.out.println("Name: " + fullTime.getName());
        System.out.println("Age: " + fullTime.getAge());
        System.out.println("Department: " + fullTime.getDepartment());
        System.out.println("Position: " + fullTime.getPosition());
        System.out.println("Basic Salary: " + fullTime.getBasicSalary());
        System.out.println("Bonus: " + fullTime.getBonus());
        System.out.println("Calculated Salary: " + fullTime.calculateSalary());
        System.out.println("Employee Type: " + fullTime.getEmployeeType());

        // Test setters
        fullTime.setBasicSalary(1500);
        fullTime.setBonus(500);

        System.out.println("\nAfter updating salary values:");
        System.out.println("Basic Salary: " + fullTime.getBasicSalary());
        System.out.println("Bonus: " + fullTime.getBonus());
        System.out.println("Calculated Salary: " + fullTime.calculateSalary());


        System.out.println("\n===== PART-TIME EMPLOYEE TEST =====");

        PartTimeEmployee partTime = new PartTimeEmployee(
                2,
                "Ali",
                22,
                "HR",
                "Assistant",
                50,
                30
        );

        System.out.println("ID: " + partTime.getId());
        System.out.println("Name: " + partTime.getName());
        System.out.println("Age: " + partTime.getAge());
        System.out.println("Department: " + partTime.getDepartment());
        System.out.println("Position: " + partTime.getPosition());
        System.out.println("Hourly Rate: " + partTime.getHourlyRate());
        System.out.println("Hours Worked: " + partTime.getHoursWorked());
        System.out.println("Calculated Salary: " + partTime.calculateSalary());
        System.out.println("Employee Type: " + partTime.getEmployeeType());

        // Test setters
        partTime.setHourlyRate(60);
        partTime.setHoursWorked(40);

        System.out.println("\nAfter updating hourly rate and hours:");
        System.out.println("Hourly Rate: " + partTime.getHourlyRate());
        System.out.println("Hours Worked: " + partTime.getHoursWorked());
        System.out.println("Calculated Salary: " + partTime.calculateSalary());


        System.out.println("\n===== POLYMORPHISM TEST =====");

        Employee employee1 = fullTime;
        Employee employee2 = partTime;

        System.out.println(employee1.getName() + " salary: "
                + employee1.calculateSalary());

        System.out.println(employee1.getEmployeeType());

        System.out.println(employee2.getName() + " salary: "
                + employee2.calculateSalary());

        System.out.println(employee2.getEmployeeType());


        System.out.println("\n===== VALIDATION TEST =====");

        try {
            fullTime.setBonus(-500);
        } catch (IllegalArgumentException e) {
            System.out.println("Negative bonus rejected: " + e.getMessage());
        }

        try {
            partTime.setHourlyRate(-10);
        } catch (IllegalArgumentException e) {
            System.out.println("Negative hourly rate rejected: " + e.getMessage());
        }

        try {
            partTime.setHoursWorked(-5);
        } catch (IllegalArgumentException e) {
            System.out.println("Negative hours rejected: " + e.getMessage());
        }

        try {
            fullTime.setBasicSalary(-100);
        } catch (IllegalArgumentException e) {
            System.out.println("Negative basic salary rejected: " + e.getMessage());
        }

        System.out.println("\n===== ALL TESTS COMPLETED =====");
    }
}
