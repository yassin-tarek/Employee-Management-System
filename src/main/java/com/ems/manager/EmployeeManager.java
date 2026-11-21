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
        List<Employee> loaded = fileHandler.loadEmployees();   // should return an empty list if no file yet
        if (loaded != null) {
            employees = new ArrayList<>(loaded);
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
        return new ArrayList<>(employees);   // copy, so the GUI can't change the real list
    }

    // ---------- add ----------

    public boolean addEmployee(Employee e) {
        if (e == null || searchEmployee(e.getId()) != null) {   // null or duplicate ID
            return false;
        }
        employees.add(e);
        if (!save()) {
            employees.remove(e);    // undo, so list and file match
            return false;
        }
        return true;
    }

    // ---------- search / filter ----------

    public Employee searchEmployee(int id) {   // by ID
        int i = indexOf(id);
        return (i == -1) ? null : employees.get(i);
    }

    public List<Employee> searchEmployees(String key) {   // by ID or name
        List<Employee> result = new ArrayList<>();
        if (key == null) {
            return result;
        }
        String k = key.trim().toLowerCase();
        for (Employee e : employees) {
            if (String.valueOf(e.getId()).equals(k) || e.getName().toLowerCase().contains(k)) {
                result.add(e);
            }
        }
        return result;
    }

    public List<Employee> filterByDepartment(String dept) {   // case-insensitive
        List<Employee> result = new ArrayList<>();
        for (Employee e : employees) {
            if (e.getDepartment().equalsIgnoreCase(dept)) {
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
            return null;                                   // not found
        }
        if (updated.getId() != id && searchEmployee(updated.getId()) != null) {
            return null;                                   // new ID belongs to someone else
        }
        Employee old = employees.get(index);
        employees.set(index, updated);                     // same slot, so the table order stays
        if (!save()) {
            employees.set(index, old);                     // undo
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
            employees.add(index, removed);                 // undo
            return false;
        }
        return true;
    }
}