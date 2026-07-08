package exception;

/**
 * Thrown when an attempt is made to issue a book that is already out
 * on loan to another (or the same) member.
 */
public class BookAlreadyIssuedException extends Exception {

    public BookAlreadyIssuedException(String isbn) {
        super("Book with ISBN " + isbn + " is already issued and not available.");
    }
}
