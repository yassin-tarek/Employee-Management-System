package com.ems.model;

import java.io.Serializable;

/**
 * Immutable department. To rename one, use DepartmentManager.renameDepartment(),
 * so the change is validated and saved to disk.
 */
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    private final int id;
    private final String name;

    public Department(int id, String name) {
        if (id <= 0) {
            throw new IllegalArgumentException("Department ID must be a positive integer.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Department name cannot be null or empty.");
        }
        this.id = id;
        this.name = name.trim();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    // Used by ComboBox / ListView to display the department
    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Department)) return false;
        return id == ((Department) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}