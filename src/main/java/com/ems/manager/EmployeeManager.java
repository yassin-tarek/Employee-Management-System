package com.ems.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ems.io.FileHandler;
import com.ems.model.Employee;

public class EmployeeManager {

    private List<Employee> employees = new ArrayList<>();
    private final FileHandler fileHandler = new FileHandler();

    public EmployeeManager() {
        List<Employee> loaded = fileHandler.loadEmployees();

        if (loaded != null) {
            employees = new ArrayList<>(loaded);
        }
    }

    // ---------- helpers ----------

    private int indexOf(int id) {
        for (int i = 0; i < employees.size(); i++) {

            Employee e = employees.get(i);

            if (e != null && e.getId() == id) {
                return i;
            }
        }

        return -1;
    }

    private boolean save() {
        try {
            fileHandler.saveEmployees(employees);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public int getNextId() {
        int max = 0;

        for (Employee e : employees) {
            if (e != null && e.getId() > max) {
                max = e.getId();
            }
        }

        return max + 1;
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees);
    }

    // ---------- add ----------

    public boolean addEmployee(Employee e) {

        if (e == null || searchEmployee(e.getId()) != null) {
            return false;
        }

        employees.add(e);

        if (!save()) {
            employees.remove(e);
            return false;
        }

        return true;
    }

    // ---------- search / filter ----------

    public Employee searchEmployee(int id) {
        int i = indexOf(id);

        return (i == -1) ? null : employees.get(i);
    }

    public List<Employee> searchEmployees(String key) {

        List<Employee> result = new ArrayList<>();

        if (key == null) {
            return result;
        }

        String k = key.trim().toLowerCase();

        for (Employee e : employees) {

            // Skip null employee objects
            if (e == null) {
                continue;
            }

            // Search by ID
            if (String.valueOf(e.getId()).equals(k)) {
                result.add(e);
                continue;
            }

            // Search by name safely
            String name = e.getName();

            if (name != null && name.toLowerCase().contains(k)) {
                result.add(e);
            }
        }

        return result;
    }

    public List<Employee> filterByDepartment(String dept) {

        List<Employee> result = new ArrayList<>();

        if (dept == null) {
            return result;
        }

        for (Employee e : employees) {

            // Skip null employee objects
            if (e == null) {
                continue;
            }

            // Check department safely
            String employeeDepartment = e.getDepartment();

            if (employeeDepartment != null
                    && dept.equalsIgnoreCase(employeeDepartment)) {

                result.add(e);
            }
        }

        return result;
    }

    // ---------- update ----------

    public Employee updateEmployee(int id, Employee updated) {

        if (updated == null) {
            return null;
        }

        int index = indexOf(id);

        if (index == -1) {
            return null;
        }

        if (updated.getId() != id
                && searchEmployee(updated.getId()) != null) {
            return null;
        }

        Employee old = employees.get(index);

        employees.set(index, updated);

        if (!save()) {
            employees.set(index, old);
            return null;
        }

        return updated;
    }

    // ---------- delete ----------

    public boolean deleteEmployee(int id) {

        int index = indexOf(id);

        if (index == -1) {
            return false;
        }

        Employee removed = employees.remove(index);

        if (!save()) {
            employees.add(index, removed);
            return false;
        }

        return true;
    }
}
