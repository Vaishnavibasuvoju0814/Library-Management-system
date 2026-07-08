package util;

import model.Book;

import java.util.Comparator;

/**
 * Reusable Comparator instances for sorting books by fields other than
 * the natural (title) order defined by Book implementing Comparable.
 * Demonstrates the Comparable vs Comparator distinction, plus lambda /
 * method-reference based comparator construction.
 */
public final class BookComparators {

    public static final Comparator<Book> BY_AUTHOR = Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<Book> BY_YEAR = Comparator.comparingInt(Book::getPublicationYear);

    public static final Comparator<Book> BY_YEAR_DESC = BY_YEAR.reversed();

    private BookComparators() {
        // utility class - no instances
    }
}
