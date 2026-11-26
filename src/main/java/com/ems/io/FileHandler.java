package com.ems.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.ems.model.Department;
import com.ems.model.Employee;

public class FileHandler {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path IMAGES_DIR = DATA_DIR.resolve("images");
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

    public boolean departmentsFileExists() {
        return Files.exists(DEPARTMENTS_FILE);
    }

    // ---------- profile pictures ----------

    /**
     * Copies the chosen picture into data/images so it keeps working even if the
     * original file is moved or deleted. Returns the path to store in Employee.imagePath.
     */
    public String saveProfileImage(Path source, int employeeId) throws IOException {
        Files.createDirectories(IMAGES_DIR);

        String fileName = source.getFileName().toString();
        int dot = fileName.lastIndexOf('.');
        String extension = (dot >= 0) ? fileName.substring(dot).toLowerCase(Locale.ROOT) : "";

        Path target = IMAGES_DIR.resolve("emp_" + employeeId + extension);
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        return target.toString();
    }

    /** Deletes a picture only if it lives inside data/images (never the user's own files). */
    public void deleteProfileImage(String imagePath) {
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return;
        }
        try {
            Path file = Paths.get(imagePath).toAbsolutePath().normalize();
            Path imagesDir = IMAGES_DIR.toAbsolutePath().normalize();
            if (file.startsWith(imagesDir)) {
                Files.deleteIfExists(file);
            }
        } catch (IOException | RuntimeException ex) {
            System.err.println("Could not delete image " + imagePath + ": " + ex.getMessage());
        }
    }

    // ---------- shared helpers ----------

    // Writes to a temp file first, then replaces the real file in one step.
    // A crash while saving can no longer leave a half-written (corrupt) data file.
    private void write(Path file, List<?> items) throws IOException {
        Files.createDirectories(DATA_DIR);

        Path temp = file.resolveSibling(file.getFileName() + ".tmp");
        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(temp))) {
            out.writeObject(new ArrayList<>(items));   // ArrayList is serializable
        }

        try {
            Files.move(temp, file,
                    StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> read(Path file) {
        if (!Files.exists(file)) {
            return new ArrayList<>();   // first launch: start empty
        }
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(file))) {
            Object data = in.readObject();
            if (data instanceof List<?>) {
                return new ArrayList<>((List<T>) data);
            }
            throw new IOException("Unexpected data in file.");
        } catch (IOException | ClassNotFoundException | ClassCastException ex) {
            System.err.println("Could not read " + file + ": " + ex.getMessage());
            backupUnreadableFile(file);
            return new ArrayList<>();
        }
    }

    // Keeps an unreadable file as a backup instead of letting the next save overwrite it
    private void backupUnreadableFile(Path file) {
        try {
            Path backup = file.resolveSibling(
                    file.getFileName() + ".corrupt-" + System.currentTimeMillis());
            Files.move(file, backup);
            System.err.println("Unreadable file kept as " + backup);
        } catch (IOException ex) {
            System.err.println("Could not back up " + file + ": " + ex.getMessage());
        }
    }
}