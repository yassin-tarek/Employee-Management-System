# Employee Management System

A desktop application for managing employees and departments, built using **Java** and **JavaFX**.

The application features a modern purple-themed interface, local file-based data storage, and employee management tools without requiring a database.

---

## Features

### Authentication
- Login system with a demo administrator account.

### Dashboard
- Total number of employees.
- Full-time and part-time employee statistics.
- Total payroll.
- Employees-per-department chart.
- Recently added employees.

### Employee Management
- Add and edit employee information.
- Upload profile pictures.
- Manage personal and job information.
- Dynamic salary fields based on employee type.
- Support for full-time and part-time employees.

### Employee Directory
- Search employees by name or ID.
- Filter by department and employee type.
- Sort table columns.
- View, edit, and delete employees directly from the list.

### Employee Profiles
- Detailed employee information.
- Employment duration.
- Salary breakdown.

### Department Management
- Add new departments.
- Rename existing departments.
- Delete departments.
- Prevent deleting departments that still contain employees.

### Data Validation
- Required field validation.
- Email format validation.
- Phone number validation.
- Age range validation.
- Duplicate email detection.
- Salary and numeric field validation.

### Data Safety
- Local file-based storage.
- Safe file replacement using temporary files.
- Corrupted data file protection.
- Automatic creation of required data directories.

---

## Technologies

| Area | Technology |
|---|---|
| Programming Language | Java 17 |
| GUI Framework | JavaFX 21 |
| UI Layout | FXML |
| UI Design | Scene Builder |
| Styling | JavaFX CSS |
| Build Tool | Apache Maven |
| Module System | Java Module System |
| Data Storage | Java Object Serialization |

---

## Requirements

- **JDK 17 or newer**
- **Apache Maven 3.8 or newer**
- Internet connection on the first run to download Maven dependencies

Verify your installation:

```bash
java -version
mvn -version
```

---

## How to Run

### 1. Clone the repository

```bash
git clone <your-repository-url>
cd Employee-Management-System
```

### 2. Run the application

From the directory containing `pom.xml`, execute:

```bash
mvn clean javafx:run
```

Maven will download the required dependencies and launch the application.

---

## Demo Account

| Username | Password |
|---|---|
| `admin` | `admin` |

> This account is intended for demonstration purposes only.

---

## Project Structure

```text
Employee-Management-System/
├── pom.xml
├── src/
│   └── main/
│       ├── java/
│       │   ├── module-info.java
│       │   └── com/
│       │       └── ems/
│       │           ├── Main.java
│       │           ├── Navigator.java
│       │           ├── AppContext.java
│       │           ├── Avatars.java
│       │           ├── Dialogs.java
│       │           │
│       │           ├── model/
│       │           │   ├── Employee.java
│       │           │   ├── FullTimeEmployee.java
│       │           │   ├── PartTimeEmployee.java
│       │           │   ├── Department.java
│       │           │   └── EmployeeType.java
│       │           │
│       │           ├── manager/
│       │           │   ├── EmployeeManager.java
│       │           │   └── DepartmentManager.java
│       │           │
│       │           ├── io/
│       │           │   └── FileHandler.java
│       │           │
│       │           └── controller/
│       │               └── [Page Controllers]
│       │
│       └── resources/
│           └── com/
│               └── ems/
│                   ├── view/
│                   │   └── [FXML Files]
│                   │
│                   └── css/
│                       ├── style.css
│                       └── [Page Stylesheets]
│
├── UI/
└── data/                     # Generated at runtime
    ├── employees.dat
    ├── departments.dat
    └── images/
```

---

## Architecture & Design

The application follows a layered architecture inspired by **MVC (Model-View-Controller)**, separating data, business logic, file handling, and the user interface.

### Model

Represents the application's core entities:

- `Employee` — Abstract base class for employees.
- `FullTimeEmployee` — Represents full-time employees and calculates their salaries.
- `PartTimeEmployee` — Represents part-time employees and calculates their salaries.
- `Department` — Represents company departments.
- `EmployeeType` — Defines the available employee types.

### Manager Layer

Handles business logic and data management:

- `EmployeeManager` — Manages employees, validation rules, employee operations, and statistics.
- `DepartmentManager` — Manages departments, department operations, and related business rules.

### File Handling

`FileHandler` is responsible for saving and loading application data.

It implements safer saving by writing data to a temporary file before replacing the original file. If a data file cannot be read, the application preserves it as a backup rather than immediately overwriting it.

### Controllers & Views

- **FXML** defines the UI layout.
- **JavaFX Controllers** handle user interactions.
- Controllers communicate with the managers to perform application operations.
- **CSS** controls the visual appearance of the application.
- `Navigator` manages page transitions.
- `AppContext` provides shared application managers.

---

## Object-Oriented Programming

The project demonstrates several core OOP concepts:

- **Abstraction** — `Employee` defines common employee properties and behavior.
- **Inheritance** — Full-time and part-time employees inherit from the base employee class.
- **Polymorphism** — Different employee types implement their own salary calculations.
- **Encapsulation** — Employee data is protected through controlled access and validation.
- **Enums** — Used to represent predefined values such as employee types.

---

## Data Storage

The application stores data locally using Java object serialization.

Data is automatically created on the first launch inside a `data/` directory relative to the application's working directory.

```text
data/
├── employees.dat
├── departments.dat
└── images/
    └── Employee Profile Pictures
```

### Storage Behavior

- Five default departments are created on the first launch.
- Employee and department data are stored in `.dat` files.
- Profile pictures are stored locally.
- Data files are saved using a temporary-file replacement strategy.
- Unreadable data files are preserved with a `.corrupt-<timestamp>` suffix.
- The application does not require a database.

### Resetting the Application

To reset the application to its initial state, close the application and delete the `data/` directory.

The application will recreate the default data on its next launch.

The `data/` directory is excluded from Git through `.gitignore` to prevent committing personal or generated application data.

---

## Validation Rules

| Field | Validation |
|---|---|
| Name | Required |
| Position | Required |
| Department | Required |
| Age | Between 16 and 100 |
| Email | Valid format and unique per employee |
| Phone | 7–20 characters; digits, `+`, `(`, `)`, `-`, and spaces |
| Salary | Valid non-negative number |
| Bonus | Valid non-negative number |
| Hourly Rate | Valid non-negative number |
| Working Hours | Valid non-negative number |
| Profile Picture | PNG, JPG, or GIF; maximum 5 MB |
| Department Name | Required and unique, case-insensitive |
| Department Deletion | Not allowed while employees are assigned to it |

---

## License

This project was developed for educational and portfolio purposes.