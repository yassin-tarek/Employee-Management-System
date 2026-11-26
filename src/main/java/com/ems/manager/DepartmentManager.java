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
            for (Department d : loaded) {
                if (d != null) {
                    departments.add(d);
                }
            }
        }
        // Defaults only on the very first launch. If the user deleted every department
        // on purpose, the saved (empty) file exists and the defaults must not come back.
        if (departments.isEmpty() && !fileHandler.departmentsFileExists()) {
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
            System.err.println("Could not save departments: " + ex.getMessage());
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

    // True if another department (not excludeId) already uses this name, ignoring case
    private boolean nameExists(String name, int excludeId) {
        String wanted = name.trim();
        for (Department d : departments) {
            if (d.getId() != excludeId && d.getName().equalsIgnoreCase(wanted)) {
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
        if (d == null) {
            return false;
        }
        if (indexOf(d.getId()) != -1 || nameExists(d.getName(), -1)) {
            return false;                                  // duplicate ID or name (ignoring case)
        }
        departments.add(d);
        if (!save()) {
            departments.remove(d);                         // undo, so list and file match
            return false;
        }
        return true;
    }

    // Convenience for the Departments page: the manager picks the ID
    public boolean addDepartment(String name) {
        try {
            return addDepartment(new Department(getNextId(), name));
        } catch (IllegalArgumentException ex) {
            return false;                                  // empty name
        }
    }

    // ---------- rename ----------

    // Department is immutable, so renaming replaces it with a new object that has the same ID.
    // Employees store the department NAME, so after a successful rename the caller must also run
    // EmployeeManager.renameDepartment(oldName, newName).
    public boolean renameDepartment(int id, String newName) {
        int index = indexOf(id);
        if (index == -1 || newName == null || newName.trim().isEmpty()) {
            return false;
        }
        if (nameExists(newName, id)) {
            return false;
        }
        Department old = departments.get(index);
        departments.set(index, new Department(id, newName));
        if (!save()) {
            departments.set(index, old);                   // undo
            return false;
        }
        return true;
    }

    // ---------- delete ----------

    // The caller should first check EmployeeManager.countInDepartment(name) == 0,
    // otherwise employees are left with a department that no longer exists.
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

    public Department getById(int id) {
        int i = indexOf(id);
        return (i == -1) ? null : departments.get(i);
    }

    public Department getByName(String name) {
        if (name == null) {
            return null;
        }
        for (Department d : departments) {
            if (d.getName().equalsIgnoreCase(name.trim())) {
                return d;
            }
        }
        return null;
    }

    public String[] getNames() {
        String[] names = new String[departments.size()];
        for (int i = 0; i < departments.size(); i++) {
            names[i] = departments.get(i).getName();
        }
        return names;
    }
}