# Advanced File System (Visitor Pattern)

##  Project Overview
This project is an in-memory hierarchical file system developed for **Assignment 06** in the **Software Design Patterns** course. It demonstrates the practical application of the **Visitor Behavioral Design Pattern** to perform independent operations over an object structure without modifying the elements themselves.

The project strictly adheres to Clean Code principles, utilizing **Double Dispatch** to avoid runtime type checking (`instanceof`) and preserving the integrity of the file system hierarchy.

---

##  Architecture & Features

### 1. Element Hierarchy (`element` package)
*   **`Element`**: The base interface defining the `accept(Visitor)` contract.
*   **`FileNode`**: Represents a leaf node with a non-empty name and a non-negative byte size.
*   **`DirectoryNode`**: Represents a composite node that can hold multiple child elements. Enforces strict tree invariants (no duplicate sibling names, single parent ownership, cycle prevention).

### 2. Visitor Operations (`visitor` package)
*   **`TotalSizeVisitor`**: Accumulates the total byte size of all files within a visited subtree.
*   **`SearchVisitor`**: Finds files that match a specific name substring and meet a minimum size threshold, returning deterministically sorted, root-relative paths.
*   **`StatisticsVisitor`**: Gathers metadata, including the total count of files, directories, and the maximum nesting depth of the tree. (Added to demonstrate the Open/Closed Principle without modifying element classes).

---

##  Prerequisites
*   **Java Development Kit (JDK):** Version 17 or higher
*   **Apache Maven:** Version 3.8 or higher

---

##  How to Build and Run

### Run Automated Test Suite (T1 - T8)
The project includes a comprehensive JUnit 5 test suite covering double dispatch, path generation, filter combination, state isolation, and invalid mutation rejections. 

To execute all tests from the command line, run:
```bash
mvn test
```

### Run Executable Demonstration
To see the file system in action with a sample tree and the output of all three visitors, run the `Main` class using Maven:
```bash
mvn compile exec:java
```

### Sample Output
```text
=== Advanced File System Demonstration ===

[1] Total Size Calculation:
Total size of files: 7200 bytes

[2] Search Results (name contains 'report', size >= 1000B):
 - docs/report.pdf
 - src/report_generator.java

[3] File System Statistics:
Total files: 5
Total directories: 4
Max tree depth: 2
==========================================
```
