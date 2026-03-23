<div align="center">

# 🌍 Bus Travel Management System

**Premium Bus Travel Management System**

[![Java Version](https://img.shields.io/badge/Java-17-007396.svg?style=flat&logo=java&logoColor=white)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-Build-C71A22.svg?style=flat&logo=apachemaven&logoColor=white)](https://maven.apache.org/)
[![Swing](https://img.shields.io/badge/UI-Swing_|_FlatLaf-blue.svg?style=flat)](https://www.formdev.com/flatlaf/)
[![SQLite](https://img.shields.io/badge/Database-SQLite-003B57.svg?style=flat&logo=sqlite&logoColor=white)](https://sqlite.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg?style=flat)](LICENSE)

*A robust, scalable, and modern desktop application for managing bus fleets, trips, destinations, and passenger reservations.*

[Overview](#-overview) •
[Features](#-key-features) •
[Architecture](#-architecture) •
[Installation](#-getting-started) •
[Tech Stack](#-tech-stack) •
[Security](#-security-features)

---

</div>

## 📖 Overview

**Bus Travel Management System** is an enterprise-grade travel management solution built in Java. It provides administrators and receptionists with powerful tools to streamline bus organization, trip scheduling, and ticket reservations. Featuring a **modern UI powered by FlatLaf**, it delivers an intuitive and beautiful experience while abstracting the complexities of transport logistics behind a reliable MVC architecture.

## ✨ Key Features

- 🚌 **Fleet Management**: Track buses, capacities, models, and assignments.
- 🗺️ **Dynamic Dispatching**: Manage intricate trips with multi-stop destinations.
- 🎟️ **Ticketing System**: Intuitive reservation module ensuring no overbooking.
- 👥 **User Roles**: Distinct permissions for Administrators vs. Receptionists.
- 🌓 **Adaptive Theming**: Seamless transitioning between Dark and Light modes.
- 🛡️ **Hardened Security**: BCrypt password hashing and protection against SQL injections.
- 📊 **Insightful Logging**: Integrated Log4j2 tracking for robust auditability.

## 🏗️ Architecture

Adopting a strict **Model-View-Controller (MVC)** design pattern, the system is highly modular, ensuring sustainable maintainability and scalability.

```
src/main/java/com/bustravel/
├── App.java                   # Application Entry Point
├── model/                     # Core Business Entities (Bus, Voyage, Passager, etc.)
├── dao/                       # Data Access Objects handling SQLite transactions
├── service/                   # High-level business logic (e.g., AuthService)
├── ui/                        # Swing Components and CardLayout Views
│   └── theme/                 # Centralized UIUtils and FlatLaf ThemeManager
└── utils/                     # Global utilities (DBConnection, ValidationUtils, Logging)
```

## 🛠️ Getting Started

### Prerequisites

To build and run this application, you will need:
- **JDK 17** or newer installed sequentially in your environment variables.
- **Apache Maven 3.6+** to handle dependencies and the build lifecycle.

### Installation & Execution

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/Bus-Travel-Management-System.git
   cd Bus-Travel-Management-System
   ```

2. **Compile and Build**
   Leverage Maven to download dependencies and compile the code:
   ```bash
   mvn clean compile
   ```

3. **Run the Application**
   Start the application using Maven's exec plugin:
   ```bash
   mvn exec:java
   ```

4. **Generate Executable JAR (Optional)**
   Create a portable, standalone "fat" JAR containing all dependencies:
   ```bash
   mvn package
   java -jar target/bus-travel-system-1.0.0.jar
   ```

## 🎨 Tech Stack

| Category        | Technology / Library | Description |
|-----------------|----------------------|-------------|
| **Core**        | Java 17              | Robust, object-oriented language. |
| **Database**    | SQLite               | Lightweight, serverless relational database. |
| **UI Engine**   | Java Swing + FlatLaf | Modernized Look & Feel framework. |
| **Security**    | jBCrypt              | Industry standard password hashing. |
| **Logging**     | Log4j2               | Asynchronous performance logging. |
| **Build & CI**  | Maven + GH Actions   | Automated dependencies & workflows. |

## 🛡️ Security Features

- **Authentication Logic Separation**: `AuthService` abstracts database authentication completely from the UI.
- **PreparedStatement Hardening**: Mitigation against SQL injection via parameterized queries.
- **Data Integrity Validation**: Centralized regex validations (`ValidationUtils`) protecting the DB layer from malformed inputs.
- **BCrypt Encryption**: One-way salting and hashing protocol securing user credentials.

---

### Author

Built with ❤️ by **[Your Name/Team]**  
*For questions, support, or improvements, please submit an Issue or a Pull Request.*
