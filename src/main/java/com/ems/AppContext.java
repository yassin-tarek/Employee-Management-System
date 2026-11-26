package com.ems;

import com.ems.manager.DepartmentManager;
import com.ems.manager.EmployeeManager;
import com.ems.model.Employee;

/**
 * One shared place for the managers, so every page works on the SAME data.
 * (If each controller did "new EmployeeManager()", each page would have its own copy
 * of the list and they would overwrite each other's changes in the file.)
 */
public final class AppContext {

    private static final EmployeeManager EMPLOYEE_MANAGER = new EmployeeManager();
    private static final DepartmentManager DEPARTMENT_MANAGER = new DepartmentManager();

    // Used to pass data between pages (Employee List -> Profile / Edit)
    private static Employee selectedEmployee;
    private static boolean editMode;

    private AppContext() {
    }

    public static EmployeeManager employees() {
        return EMPLOYEE_MANAGER;
    }

    public static DepartmentManager departments() {
        return DEPARTMENT_MANAGER;
    }

    public static Employee getSelectedEmployee() {
        return selectedEmployee;
    }

    public static void setSelectedEmployee(Employee employee) {
        selectedEmployee = employee;
    }

    public static boolean isEditMode() {
        return editMode;
    }

    public static void setEditMode(boolean value) {
        editMode = value;
    }
}