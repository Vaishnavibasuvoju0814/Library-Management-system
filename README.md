# Library Management System

A console-based **Core Java** (Java 17) project built to demonstrate strong
OOP fundamentals and general-purpose Java skills — no Spring Boot, no
database, no GUI. Everything runs in the terminal and data is persisted to
plain text files.

## How to Run

Requires JDK 17+.

```bash
# From the project root (the folder containing src/)

# 1. Compile
find src -name "*.java" > sources.txt
javac -d out @sources.txt

# 2. Run
java -cp out Main
```

On Windows (PowerShell), replace step 1 with:
```powershell
Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName } > sources.txt
javac -d out "@sources.txt"
```

Data is saved to / loaded from a `data/` folder created automatically next
to wherever you run the `java` command from. The app auto-loads any
existing data on startup and auto-saves on exit; you can also trigger
Save/Load manually from the menu (options 9 and 10).

## Project Structure

```
library-management-system/
│
├── src/
│   ├── Main.java                          # Console UI / menu loop only
│   │
│   ├── model/
│   │   ├── Person.java                    # abstract base class
│   │   ├── Book.java                      # Comparable + Reportable
│   │   ├── Member.java                    # extends Person, Reportable
│   │   └── Librarian.java                 # extends Person
│   │
│   ├── service/
│   │   ├── LibraryService.java            # business logic / rules
│   │   └── Reportable.java                # interface for polymorphic reports
│   │
│   ├── repository/
│   │   ├── Repository.java                # generic in-memory store <T, ID>
│   │   └── LibraryRepository.java         # composes two Repository<T,ID>
│   │
│   ├── exception/
│   │   ├── BookNotFoundException.java
│   │   ├── BookAlreadyIssuedException.java
│   │   └── MemberNotFoundException.java
│   │
│   └── util/
│       ├── FileUtil.java                  # save/load books & members
│       └── BookComparators.java           # reusable Comparator constants
│
└── data/                                  # created at runtime (books.txt, members.txt)
```

## Architecture (Layered Design)

The project follows a simple **layered architecture**, the same shape used
in real Spring-style apps but written with plain Java:

- **Main (UI layer)** — reads console input, calls the service layer,
  prints results. Contains *zero* business logic.
- **service (business layer)** — `LibraryService` owns all the rules:
  issuing/returning books, fine calculation, search, sorting. `Reportable`
  is a small interface that lets the service treat `Book` and `Member`
  polymorphically when generating summaries.
- **repository (data-access layer)** — `LibraryRepository` is the only
  class that touches storage. Internally it delegates to a generic
  `Repository<T, ID>` so the same CRUD code is reused for both books
  (keyed by ISBN) and members (keyed by member ID).
- **model** — plain domain classes (`Book`, `Member`, `Librarian`), plus
  the shared abstract class `Person`.
- **exception** — checked, custom exceptions for expected failure cases
  (book missing, member missing, book already on loan), handled explicitly
  by the UI layer with clear messages instead of stack traces.
- **util** — cross-cutting helpers: `FileUtil` (persistence) and
  `BookComparators` (reusable sort orders).

Each layer only talks to the layer directly below it (`Main → service →
repository → model/util`), which is why the business rules in
`LibraryService` could be unit-tested without touching the console or file
system at all.

## Features

| # | Feature              | Where implemented |
|---|-----------------------|--------------------|
| 1 | Add Book               | `LibraryService.addBook(...)` (overloaded) |
| 2 | View Books             | `LibraryService.getAllBooksSortedBy...` |
| 3 | Search Book            | `LibraryService.searchBooks(...)` (overloaded) |
| 4 | Remove Book            | `LibraryService.removeBook(String)` |
| 5 | Register Member        | `LibraryService.registerMember(...)` (overloaded) |
| 6 | View Members           | `LibraryService.getAllMembers()` + `Reportable` |
| 7 | Issue Book             | `LibraryService.issueBook(...)` |
| 8 | Return Book            | `LibraryService.returnBook(...)` |
| 9 | Fine Calculation       | `LibraryService.calculateFine(...)` (Rs. 5/day, 14-day loan period) |
| 10 | Save Data to File     | `FileUtil.saveBooks/saveMembers` |
| 11 | Load Data from File   | `FileUtil.loadBooks/loadMembers` |

## OOP & Core Java Concepts Demonstrated

| Concept | Where |
|---|---|
| **Encapsulation** | Private fields with getters/setters throughout `model/` |
| **Inheritance** | `Member extends Person`, `Librarian extends Person` |
| **Abstraction** | `abstract class Person` with `abstract getRole()` |
| **Interface** | `Reportable` implemented by `Book` and `Member` |
| **Polymorphism** | `LibraryService.buildReport(Reportable r)` calls the correct `generateReport()` at runtime regardless of concrete type |
| **Method Overloading** | `addBook(Book)` / `addBook(isbn, title, author, year)`; `searchBooks(keyword)` / `searchBooks(field, value)`; `registerMember(Member)` / `registerMember(id, name, email, phone)` |
| **Method Overriding** | `toString()` and `getRole()` overridden in `Member`/`Librarian`; `generateReport()` overridden per class |
| **Collections** | `ArrayList` (search results, issued-book lists), `HashMap` (inside generic `Repository<T, ID>`) |
| **Generics** | `Repository<T, ID>` reused for both `Book`/ISBN and `Member`/memberId |
| **Comparable** | `Book implements Comparable<Book>` (natural order = title) |
| **Comparator** | `BookComparators.BY_AUTHOR`, `BY_YEAR` used in the "View Books" sort menu |
| **Streams** | Filtering/searching/sorting in `LibraryService` (`.stream().filter().collect()`) |
| **Lambda Expressions** | Stream predicates, `Comparator.comparing(Book::getAuthor, ...)`, `list.forEach(...)` |
| **Exception Handling** | try/catch around all risky operations in `Main` |
| **Custom Exceptions** | `BookNotFoundException`, `BookAlreadyIssuedException`, `MemberNotFoundException` (all checked) |
| **File Handling** | `FileUtil` uses `java.nio.file` + try-with-resources to persist/restore state as pipe-delimited text |

## Sample Session

```
1  -> Add Book       -> ISBN, Title, Author, Year
5  -> Register Member -> Member ID, Name, Email, Phone
7  -> Issue Book      -> gives the book a 14-day due date
8  -> Return Book     -> Rs. 5/day fine is charged automatically if late
9  -> Save Data       -> writes data/books.txt and data/members.txt
0  -> Exit            -> auto-saves before quitting
```

