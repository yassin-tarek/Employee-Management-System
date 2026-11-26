package com.ems.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.ems.io.FileHandler;
import com.ems.model.Employee;
import com.ems.model.EmployeeType;

public class EmployeeManager {

    private List<Employee> employees = new ArrayList<>();
    private final FileHandler fileHandler = new FileHandler();

    public EmployeeManager() {
        List<Employee> loaded = fileHandler.loadEmployees();

        if (loaded != null) {
            for (Employee e : loaded) {
                if (e != null) {                 // drop null entries once, here
                    employees.add(e);
                }
            }
        }
    }

    // ---------- helpers ----------

    private int indexOf(int id) {
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getId() == id) {
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
            System.err.println("Could not save employees: " + ex.getMessage());
            return false;
        }
    }

    public int getNextId() {
        int max = 0;

        for (Employee e : employees) {
            if (e.getId() > max) {
                max = e.getId();
            }
        }

        return max + 1;
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees);
    }

    // True if another employee (not excludeId) already uses this email.
    // Call it from the form BEFORE add/update so you can show a clear message.
    public boolean isEmailTaken(String email, int excludeId) {
        if (email == null) {
            return false;
        }
        String wanted = email.trim();
        for (Employee e : employees) {
            if (e.getId() != excludeId && e.getEmail() != null
                    && e.getEmail().equalsIgnoreCase(wanted)) {
                return true;
            }
        }
        return false;
    }

    // ---------- add ----------

    public boolean addEmployee(Employee e) {

        if (e == null || indexOf(e.getId()) != -1 || isEmailTaken(e.getEmail(), e.getId())) {
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

        String k = key.trim().toLowerCase(Locale.ROOT);

        for (Employee e : employees) {

            // Search by ID
            if (String.valueOf(e.getId()).equals(k)) {
                result.add(e);
                continue;
            }

            // Search by name (an empty key matches everyone)
            String name = e.getName();

            if (name != null && name.toLowerCase(Locale.ROOT).contains(k)) {
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
            if (dept.trim().equalsIgnoreCase(e.getDepartment())) {
                result.add(e);
            }
        }

        return result;
    }

    public List<Employee> filterByType(EmployeeType type) {

        List<Employee> result = new ArrayList<>();

        for (Employee e : employees) {
            if (e.getEmployeeType() == type) {
                result.add(e);
            }
        }

        return result;
    }

    // ---------- update ----------

    // Replaces the employee that has this ID. The ID can't change (other data refers to it).
    // Employee has no setter for ID and the type may change (full-time <-> part-time),
    // so the edit form builds a new object with the SAME ID and passes it here.
    public Employee updateEmployee(int id, Employee updated) {

        if (updated == null || updated.getId() != id) {
            return null;
        }

        int index = indexOf(id);

        if (index == -1 || isEmailTaken(updated.getEmail(), id)) {
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

        fileHandler.deleteProfileImage(removed.getImagePath());   // no orphan pictures
        return true;
    }

    // ---------- departments ----------

    public int countInDepartment(String dept) {
        return filterByDepartment(dept).size();
    }

    // Call after DepartmentManager.renameDepartment() succeeded.
    public boolean renameDepartment(String oldName, String newName) {

        if (oldName == null || newName == null || newName.trim().isEmpty()) {
            return false;
        }

        List<Employee> affected = filterByDepartment(oldName);

        if (affected.isEmpty()) {
            return true;
        }

        List<String> previous = new ArrayList<>();

        for (Employee e : affected) {
            previous.add(e.getDepartment());
            e.setDepartment(newName);
        }

        if (!save()) {
            for (int i = 0; i < affected.size(); i++) {
                affected.get(i).setDepartment(previous.get(i));   // undo
            }
            return false;
        }

        return true;
    }

    // ---------- statistics (Dashboard) ----------

    public int getEmployeeCount() {
        return employees.size();
    }

    public int countByType(EmployeeType type) {
        return filterByType(type).size();
    }

    public double getTotalPayroll() {
        double total = 0;

        for (Employee e : employees) {
            total += e.calculateSalary();
        }

        return total;
    }

    public double getAverageSalary() {
        return employees.isEmpty() ? 0 : getTotalPayroll() / employees.size();
    }
}