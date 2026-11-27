# \# Employee Management System

# 

# A desktop application for managing a company's employees and departments, built with \*\*Java\*\* and \*\*JavaFX\*\*.

# It has a modern purple interface, saves its data to local files, and needs no database or framework.

# 

# \## Features

# 

# \- \*\*Login\*\* with a demo admin account

# \- \*\*Dashboard\*\* with summary cards (total, full-time and part-time employees, total payroll), an employees-per-department chart and a list of recently added employees

# \- \*\*Add and edit employees\*\* with a profile picture, personal and job information, and a salary section that changes with the employee type

# \- \*\*Employee list\*\* with search by name or ID, filters by department and type, sortable columns, and View / Edit / Delete on every row

# \- \*\*Employee profile\*\* with all the details, time with the company and a salary breakdown

# \- \*\*Departments\*\* page to add, rename and delete departments

# \- \*\*Input validation\*\* for required fields, email and phone format, age range and duplicate emails

# \- \*\*Safe data saving\*\* that never leaves a half-written file behind

# 

# \## Screenshots

# 

# Add your screenshots to the `UI/` folder and link them here.

# 

# | Login | Dashboard |

# |---|---|

# | !\[Login](UI/login.png) | !\[Dashboard](UI/dashboard.png) |

# 

# | Employees | Profile |

# |---|---|

# | !\[Employees](UI/employees.png) | !\[Profile](UI/profile.png) |

# 

# \## Technologies

# 

# | Area | Technology |

# |---|---|

# | Language | Java 17 |

# | GUI | JavaFX 21 |

# | Screen layouts | FXML (edited with Scene Builder) |

# | Styling | JavaFX CSS |

# | Build tool | Apache Maven |

# | Modules | Java module system (`module-info.java`) |

# | Data storage | Java object serialization (`.dat` files) |

# 

# \## Requirements

# 

# \- \*\*JDK 17 or newer\*\*

# \- \*\*Apache Maven 3.8 or newer\*\*

# \- An internet connection the first time, so Maven can download JavaFX

# 

# Check your setup:

# 

# ```

# java -version

# mvn -version

# ```

# 

# \## How to Run

# 

# 1\. Clone the repository:

# 

# &#x20;  ```

# &#x20;  git clone <your-repository-url>

# &#x20;  cd Employee-Management-System

# &#x20;  ```

# 

# 2\. Run the application from the folder that contains `pom.xml`:

# 

# &#x20;  ```

# &#x20;  mvn clean javafx:run

# &#x20;  ```

# 

# \## Demo Login

# 

# | Username | Password |

# |---|---|

# | `admin` | `admin` |

# 

# \## Project Structure

# 

# ```

# Employee-Management-System/

# ├── pom.xml

# ├── src/main/

# │   ├── java/

# │   │   ├── module-info.java

# │   │   └── com/ems/

# │   │       ├── Main.java            # starts the app

# │   │       ├── Navigator.java       # switches between pages

# │   │       ├── AppContext.java      # one shared set of managers

# │   │       ├── Avatars.java         # round profile pictures

# │   │       ├── Dialogs.java         # confirmation dialogs

# │   │       ├── model/               # Employee, FullTimeEmployee, PartTimeEmployee, Department, EmployeeType

# │   │       ├── manager/             # EmployeeManager, DepartmentManager (business rules)

# │   │       ├── io/                  # FileHandler (saving and loading)

# │   │       └── controller/          # one controller per page

# │   └── resources/com/ems/

# │       ├── view/                    # FXML files

# │       └── css/                     # style.css + one stylesheet per page

# └── UI/                              # screenshots

# ```

# 

# \## Design Overview

# 

# The code is split into layers:

# 

# \- \*\*Model\*\*: the abstract `Employee` class with the subclasses `FullTimeEmployee` and `PartTimeEmployee`, each calculating its own salary.

# \- \*\*Managers\*\*: hold the data and enforce the rules, such as unique IDs and emails, department rename and delete rules, and statistics for the dashboard.

# \- \*\*IO\*\*: `FileHandler` writes data to a temporary file first and then replaces the real file, and keeps an unreadable file as a backup instead of overwriting it.

# \- \*\*Controllers and views\*\*: JavaFX controllers connect the FXML screens to the managers (MVC).

# 

# OOP concepts used: inheritance, abstraction, polymorphism, encapsulation with validated setters, and enums.

# 

# \## Data Storage

# 

# Data is created automatically on first run, in a `data/` folder next to where the app is started:

# 

# ```

# data/

# ├── employees.dat

# ├── departments.dat

# └── images/            # profile pictures

# ```

# 

# \- Five default departments are created on the first launch.

# \- If a data file cannot be read, it is renamed to `\*.corrupt-<time>` instead of being overwritten.

# \- Delete the `data/` folder to reset the app to a clean state.

# \- `data/` should be listed in `.gitignore`, so personal data is not committed.

# 

# \## Validation Rules

# 

# | Field | Rule |

# |---|---|

# | Name, position, department | Required |

# | Age | Between 16 and 100 |

# | Email | Valid format, unique per employee |

# | Phone | 7 to 20 characters: digits, `+`, `(`, `)`, `-` and spaces |

# | Salary, bonus, hourly rate, hours | Valid non-negative numbers |

# | Profile picture | PNG, JPG or GIF, up to 5 MB |

# | Department name | Required, unique (ignoring upper and lower case) |

# | Deleting a department | Not allowed while it still has employees |

# 

