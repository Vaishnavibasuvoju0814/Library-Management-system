package model;

import service.Reportable;

import java.time.LocalDate;

/**
 * Represents a single book (single copy) in the library catalogue.
 * Demonstrates ENCAPSULATION (private fields + controlled access),
 * Comparable (natural ordering by title) and Reportable (interface
 * implementation used polymorphically by the service layer).
 */
public class Book implements Comparable<Book>, Reportable {

    private final String isbn;
    private String title;
    private String author;
    private int publicationYear;

    // Issue tracking state
    private boolean issued;
    private String issuedToMemberId;
    private LocalDate issueDate;
    private LocalDate dueDate;

    public Book(String isbn, String title, String author, int publicationYear) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.issued = false;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public boolean isIssued() {
        return issued;
    }

    public String getIssuedToMemberId() {
        return issuedToMemberId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    /** Marks this book as issued to a member and sets the loan window. */
    public void markIssued(String memberId, LocalDate issueDate, LocalDate dueDate) {
        this.issued = true;
        this.issuedToMemberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    /** Clears loan state once the book has been returned. */
    public void markReturned() {
        this.issued = false;
        this.issuedToMemberId = null;
        this.issueDate = null;
        this.dueDate = null;
    }

    /** Natural ordering: alphabetically by title (used by Collections.sort / TreeSet). */
    @Override
    public int compareTo(Book other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    /** Polymorphic report implementation required by the Reportable interface. */
    @Override
    public String generateReport() {
        String status = issued ? ("ISSUED to " + issuedToMemberId + " (due " + dueDate + ")") : "AVAILABLE";
        return String.format("[%s] \"%s\" by %s (%d) - %s", isbn, title, author, publicationYear, status);
    }

    @Override
    public String toString() {
        return String.format("%-12s | %-30s | %-20s | %-6d | %s",
                isbn, title, author, publicationYear, issued ? "ISSUED" : "AVAILABLE");
    }

    /** Serialises this book into a single pipe-delimited line for file storage. */
    public String toFileLine() {
        return String.join("|",
                isbn, title, author, String.valueOf(publicationYear),
                String.valueOf(issued),
                issuedToMemberId == null ? "" : issuedToMemberId,
                issueDate == null ? "" : issueDate.toString(),
                dueDate == null ? "" : dueDate.toString());
    }

    /** Rebuilds a Book from a line produced by {@link #toFileLine()}. */
    public static Book fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        Book book = new Book(p[0], p[1], p[2], Integer.parseInt(p[3]));
        boolean issued = Boolean.parseBoolean(p[4]);
        if (issued) {
            String memberId = p[5];
            LocalDate issueDate = LocalDate.parse(p[6]);
            LocalDate dueDate = LocalDate.parse(p[7]);
            book.markIssued(memberId, issueDate, dueDate);
        }
        return book;
    }
}
