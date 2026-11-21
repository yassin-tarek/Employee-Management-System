package com.ems.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ems.io.FileHandler;
import com.ems.model.Department;

public class DepartmentManager {

    private List<Department> departments = new ArrayList<>();
    private final FileHandler fileHandler = new FileHandler();

    public DepartmentManager() {
        List<Department> loaded = fileHandler.loadDepartments();   // empty list if no file yet
        if (loaded != null) {
            departments = new ArrayList<>(loaded);
        }
        if (departments.isEmpty()) {
            addDefaults();
        }
    }

    // ---------- helpers ----------

    private void addDefaults() {
        departments.add(new Department(1, "IT"));
        departments.add(new Department(2, "Human Resources"));
        departments.add(new Department(3, "Finance"));
        departments.add(new Department(4, "Marketing"));
        departments.add(new Department(5, "Sales"));
        save();
    }

    private boolean save() {
        try {
            fileHandler.saveDepartments(departments);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    private int indexOf(int id) {
        for (int i = 0; i < departments.size(); i++) {
            if (departments.get(i).getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private boolean nameExists(String name) {
        for (Department d : departments) {
            if (d.getName().equalsIgnoreCase(name.trim())) {
                return true;
            }
        }
        return false;
    }

    public int getNextId() {
        int max = 0;
        for (Department d : departments) {
            if (d.getId() > max) {
                max = d.getId();
            }
        }
        return max + 1;
    }

    // ---------- add ----------

    public boolean addDepartment(Department d) {
        if (d == null || d.getName() == null || d.getName().trim().isEmpty()) {
            return false;                                  // missing name
        }
        if (indexOf(d.getId()) != -1 || nameExists(d.getName())) {
            return false;                                  // duplicate ID or name (ignoring case)
        }
        departments.add(d);
        if (!save()) {
            departments.remove(d);                         // undo, so list and file match
            return false;
        }
        return true;
    }

    // ---------- delete ----------

    public boolean deleteDepartment(int id) {
        int index = indexOf(id);
        if (index == -1) {
            return false;
        }
        Department removed = departments.remove(index);
        if (!save()) {
            departments.add(index, removed);               // undo
            return false;
        }
        return true;
    }

    // ---------- read ----------

    public List<Department> getAllDepartments() {
        return new ArrayList<>(departments);               // copy, so the GUI can't change the real list
    }

    public String[] getNames() {
        String[] names = new String[departments.size()];
        for (int i = 0; i < departments.size(); i++) {
            names[i] = departments.get(i).getName();
        }
        return names;
    }
}