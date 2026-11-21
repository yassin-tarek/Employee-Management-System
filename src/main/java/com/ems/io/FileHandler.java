package com.ems.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.ems.model.Department;
import com.ems.model.Employee;

public class FileHandler {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path EMPLOYEES_FILE = DATA_DIR.resolve("employees.dat");
    private static final Path DEPARTMENTS_FILE = DATA_DIR.resolve("departments.dat");

    // ---------- employees ----------

    public void saveEmployees(List<Employee> employees) throws IOException {
        write(EMPLOYEES_FILE, employees);
    }

    public List<Employee> loadEmployees() {
        return read(EMPLOYEES_FILE);
    }

    // ---------- departments ----------

    public void saveDepartments(List<Department> departments) throws IOException {
        write(DEPARTMENTS_FILE, departments);
    }

    public List<Department> loadDepartments() {
        return read(DEPARTMENTS_FILE);
    }

    // ---------- shared helpers ----------

    private void write(Path file, List<?> items) throws IOException {
        Files.createDirectories(DATA_DIR);   // creates data/ if it doesn't exist yet
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(file))) {
            out.writeObject(new ArrayList<>(items));   // ArrayList is serializable
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> read(Path file) {
        if (!Files.exists(file)) {
            return new ArrayList<>();   // first launch: start empty
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            return (List<T>) in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Could not read " + file + ": " + ex.getMessage());
            return new ArrayList<>();
        }
    }
}