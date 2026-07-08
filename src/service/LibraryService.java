package service;

import exception.BookAlreadyIssuedException;
import exception.BookNotFoundException;
import exception.MemberNotFoundException;
import model.Book;
import model.Member;
import repository.LibraryRepository;
import util.FileUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic layer. All menu actions in Main are thin wrappers around
 * methods here - this keeps the console/UI code separate from the rules
 * that govern issuing, returning and fining books.
 */
public class LibraryService {

    private static final int LOAN_PERIOD_DAYS = 14;
    private static final double FINE_PER_DAY = 5.0; // rupees per day overdue

    private final LibraryRepository repository;

    public LibraryService(LibraryRepository repository) {
        this.repository = repository;
    }

    // ---------------- Book management ----------------

    /** Add a fully-built Book object. */
    public void addBook(Book book) {
        repository.addBook(book);
    }

    /** Overloaded convenience version: builds the Book from raw fields.
     *  Demonstrates METHOD OVERLOADING alongside addBook(Book). */
    public void addBook(String isbn, String title, String author, int year) {
        addBook(new Book(isbn, title, author, year));
    }

    public List<Book> getAllBooksSortedByTitle() {
        // Book implements Comparable<Book> (natural order = title)
        return repository.getAllBooks().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Book> getAllBooksSortedBy(Comparator<Book> comparator) {
        return repository.getAllBooks().stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /** Search by a single keyword across title and author (Streams + Lambdas). */
    public List<Book> searchBooks(String keyword) {
        String needle = keyword.toLowerCase();
        return repository.getAllBooks().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(needle)
                        || b.getAuthor().toLowerCase().contains(needle)
                        || b.getIsbn().equalsIgnoreCase(keyword))
                .collect(Collectors.toList());
    }

    /** Overloaded search: search a specific field only.
     *  Demonstrates METHOD OVERLOADING (same name, different signature). */
    public List<Book> searchBooks(String field, String value) {
        String needle = value.toLowerCase();
        return switch (field.toLowerCase()) {
            case "title" -> repository.getAllBooks().stream()
                    .filter(b -> b.getTitle().toLowerCase().contains(needle))
                    .collect(Collectors.toList());
            case "author" -> repository.getAllBooks().stream()
                    .filter(b -> b.getAuthor().toLowerCase().contains(needle))
                    .collect(Collectors.toList());
            case "isbn" -> repository.getAllBooks().stream()
                    .filter(b -> b.getIsbn().equalsIgnoreCase(value))
                    .collect(Collectors.toList());
            default -> List.of();
        };
    }

    public void removeBook(String isbn) throws BookNotFoundException {
        Book book = getBookOrThrow(isbn);
        if (book.isIssued()) {
            throw new IllegalStateException("Cannot remove a book that is currently issued.");
        }
        repository.removeBook(isbn);
    }

    // ---------------- Member management ----------------

    public void registerMember(Member member) {
        repository.addMember(member);
    }

    /** Overloaded convenience version, similar pattern to addBook(). */
    public void registerMember(String id, String name, String email, String phone) {
        registerMember(new Member(id, name, email, phone));
    }

    public List<Member> getAllMembers() {
        return repository.getAllMembers();
    }

    // ---------------- Issue / Return ----------------

    public void issueBook(String isbn, String memberId)
            throws BookNotFoundException, MemberNotFoundException, BookAlreadyIssuedException {

        Book book = getBookOrThrow(isbn);
        Member member = getMemberOrThrow(memberId);

        if (book.isIssued()) {
            throw new BookAlreadyIssuedException(isbn);
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(LOAN_PERIOD_DAYS);
        book.markIssued(memberId, issueDate, dueDate);
        member.addIssuedBook(isbn);
    }

    /**
     * Returns a book and calculates any overdue fine.
     *
     * @return the fine amount charged (0.0 if returned on time)
     */
    public double returnBook(String isbn) throws BookNotFoundException, MemberNotFoundException {
        Book book = getBookOrThrow(isbn);
        if (!book.isIssued()) {
            throw new IllegalStateException("This book is not currently issued.");
        }

        Member member = getMemberOrThrow(book.getIssuedToMemberId());
        double fine = calculateFine(book.getDueDate(), LocalDate.now());

        member.addFine(fine);
        member.removeIssuedBook(isbn);
        book.markReturned();
        return fine;
    }

    /** Pure fine-calculation function, kept separate so it is easy to unit test. */
    public double calculateFine(LocalDate dueDate, LocalDate returnDate) {
        long lateDays = ChronoUnit.DAYS.between(dueDate, returnDate);
        return lateDays > 0 ? lateDays * FINE_PER_DAY : 0.0;
    }

    // ---------------- Reports (polymorphism via Reportable) ----------------

    /** Accepts anything Reportable - Book, Member, or future entity types. */
    public String buildReport(Reportable reportable) {
        return reportable.generateReport();
    }

    // ---------------- Persistence ----------------

    public void saveData() throws IOException {
        FileUtil.saveBooks(repository.getAllBooks());
        FileUtil.saveMembers(repository.getAllMembers());
    }

    public void loadData() throws IOException {
        List<Book> books = FileUtil.loadBooks();
        List<Member> members = FileUtil.loadMembers();
        books.forEach(repository::addBook);
        members.forEach(repository::addMember);
    }

    // ---------------- Helpers ----------------

    private Book getBookOrThrow(String isbn) throws BookNotFoundException {
        return repository.findBookByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException(isbn));
    }

    private Member getMemberOrThrow(String memberId) throws MemberNotFoundException {
        return repository.findMemberById(memberId)
                .orElseThrow(() -> new MemberNotFoundException(memberId));
    }
}
