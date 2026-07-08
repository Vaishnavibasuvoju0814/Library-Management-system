import exception.BookAlreadyIssuedException;
import exception.BookNotFoundException;
import exception.MemberNotFoundException;
import model.Book;
import model.Member;
import repository.LibraryRepository;
import service.LibraryService;
import util.BookComparators;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Console entry point for the Library Management System.
 * Responsible only for user interaction (menus, input, output) - all
 * business rules live in {@link LibraryService}.
 */
public class Main {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final LibraryService LIBRARY_SERVICE = new LibraryService(new LibraryRepository());

    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("   LIBRARY MANAGEMENT SYSTEM");
        System.out.println("=====================================");

        autoLoadOnStartup();

        boolean running = true;
        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");
            switch (choice) {
                case 1 -> addBook();
                case 2 -> viewBooks();
                case 3 -> searchBook();
                case 4 -> removeBook();
                case 5 -> registerMember();
                case 6 -> viewMembers();
                case 7 -> issueBook();
                case 8 -> returnBook();
                case 9 -> saveData();
                case 10 -> loadData();
                case 0 -> running = exitApplication();
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        SCANNER.close();
    }

    private static void printMenu() {
        System.out.println("\n----------- MAIN MENU -----------");
        System.out.println(" 1. Add Book");
        System.out.println(" 2. View Books");
        System.out.println(" 3. Search Book");
        System.out.println(" 4. Remove Book");
        System.out.println(" 5. Register Member");
        System.out.println(" 6. View Members");
        System.out.println(" 7. Issue Book");
        System.out.println(" 8. Return Book");
        System.out.println(" 9. Save Data to File");
        System.out.println("10. Load Data from File");
        System.out.println(" 0. Exit");
        System.out.println("----------------------------------");
    }

    // ---------------- Book actions ----------------

    private static void addBook() {
        System.out.println("\n-- Add Book --");
        String isbn = readString("ISBN: ");
        String title = readString("Title: ");
        String author = readString("Author: ");
        int year = readInt("Publication Year: ");
        LIBRARY_SERVICE.addBook(isbn, title, author, year); // overloaded addBook(...)
        System.out.println("Book added successfully.");
    }

    private static void viewBooks() {
        System.out.println("\n-- All Books --");
        System.out.println("Sort by: 1) Title  2) Author  3) Year");
        int sortChoice = readInt("Choice: ");
        List<Book> books = switch (sortChoice) {
            case 2 -> LIBRARY_SERVICE.getAllBooksSortedBy(BookComparators.BY_AUTHOR);
            case 3 -> LIBRARY_SERVICE.getAllBooksSortedBy(BookComparators.BY_YEAR);
            default -> LIBRARY_SERVICE.getAllBooksSortedByTitle();
        };
        printBooks(books);
    }

    private static void searchBook() {
        System.out.println("\n-- Search Book --");
        System.out.println("1) Quick search (title/author/isbn)  2) Search by specific field");
        int mode = readInt("Choice: ");
        List<Book> results;
        if (mode == 2) {
            String field = readString("Field (title/author/isbn): ");
            String value = readString("Value: ");
            results = LIBRARY_SERVICE.searchBooks(field, value); // overloaded searchBooks(field, value)
        } else {
            String keyword = readString("Keyword: ");
            results = LIBRARY_SERVICE.searchBooks(keyword); // overloaded searchBooks(keyword)
        }
        printBooks(results);
    }

    private static void removeBook() {
        System.out.println("\n-- Remove Book --");
        String isbn = readString("ISBN to remove: ");
        try {
            LIBRARY_SERVICE.removeBook(isbn);
            System.out.println("Book removed successfully.");
        } catch (BookNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Cannot remove: " + e.getMessage());
        }
    }

    private static void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        System.out.printf("%-12s | %-30s | %-20s | %-6s | %s%n", "ISBN", "Title", "Author", "Year", "Status");
        books.forEach(System.out::println); // lambda / method reference over a stream-friendly list
    }

    // ---------------- Member actions ----------------

    private static void registerMember() {
        System.out.println("\n-- Register Member --");
        String id = readString("Member ID: ");
        String name = readString("Name: ");
        String email = readString("Email: ");
        String phone = readString("Phone: ");
        LIBRARY_SERVICE.registerMember(id, name, email, phone); // overloaded registerMember(...)
        System.out.println("Member registered successfully.");
    }

    private static void viewMembers() {
        System.out.println("\n-- All Members --");
        List<Member> members = LIBRARY_SERVICE.getAllMembers();
        if (members.isEmpty()) {
            System.out.println("No members found.");
            return;
        }
        members.forEach(m -> System.out.println(LIBRARY_SERVICE.buildReport(m))); // Reportable polymorphism
    }

    // ---------------- Issue / Return actions ----------------

    private static void issueBook() {
        System.out.println("\n-- Issue Book --");
        String isbn = readString("Book ISBN: ");
        String memberId = readString("Member ID: ");
        try {
            LIBRARY_SERVICE.issueBook(isbn, memberId);
            System.out.println("Book issued successfully. Due back in 14 days.");
        } catch (BookNotFoundException | MemberNotFoundException | BookAlreadyIssuedException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void returnBook() {
        System.out.println("\n-- Return Book --");
        String isbn = readString("Book ISBN: ");
        try {
            double fine = LIBRARY_SERVICE.returnBook(isbn);
            if (fine > 0) {
                System.out.printf("Book returned late. Fine charged: Rs.%.2f%n", fine);
            } else {
                System.out.println("Book returned on time. No fine.");
            }
        } catch (BookNotFoundException | MemberNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Cannot return: " + e.getMessage());
        }
    }

    // ---------------- Persistence actions ----------------

    private static void saveData() {
        try {
            LIBRARY_SERVICE.saveData();
            System.out.println("Data saved to the 'data' folder successfully.");
        } catch (IOException e) {
            System.out.println("Failed to save data: " + e.getMessage());
        }
    }

    private static void loadData() {
        try {
            LIBRARY_SERVICE.loadData();
            System.out.println("Data loaded successfully.");
        } catch (IOException e) {
            System.out.println("Failed to load data: " + e.getMessage());
        }
    }

    private static void autoLoadOnStartup() {
        try {
            LIBRARY_SERVICE.loadData();
            System.out.println("Existing data loaded from disk (if any).");
        } catch (IOException e) {
            System.out.println("No existing data loaded (" + e.getMessage() + ")");
        }
    }

    private static boolean exitApplication() {
        saveData();
        System.out.println("Thank you for using the Library Management System. Goodbye!");
        return false;
    }

    // ---------------- Input helpers ----------------

    private static String readString(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
