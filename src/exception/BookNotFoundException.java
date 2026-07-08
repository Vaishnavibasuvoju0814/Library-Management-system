package exception;

/**
 * Thrown when a book lookup by ISBN fails. A checked exception on purpose -
 * callers (the console menu) are forced to handle the "book not found"
 * case explicitly, which is good practice for user-facing operations.
 */
public class BookNotFoundException extends Exception {

    public BookNotFoundException(String isbn) {
        super("No book found with ISBN: " + isbn);
    }
}
