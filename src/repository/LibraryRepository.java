package repository;

import model.Book;
import model.Member;

import java.util.List;
import java.util.Optional;

/**
 * Data-access layer for the library. Wraps two generic {@link Repository}
 * instances (composition over inheritance) so the service layer never
 * touches raw collections directly - a common pattern that keeps storage
 * concerns separate from business logic.
 */
public class LibraryRepository {

    private final Repository<Book, String> bookStore = new Repository<>();
    private final Repository<Member, String> memberStore = new Repository<>();

    // ---- Book operations ----

    public void addBook(Book book) {
        bookStore.save(book.getIsbn(), book);
    }

    public Optional<Book> findBookByIsbn(String isbn) {
        return bookStore.findById(isbn);
    }

    public List<Book> getAllBooks() {
        return bookStore.findAll();
    }

    public void removeBook(String isbn) {
        bookStore.deleteById(isbn);
    }

    public boolean bookExists(String isbn) {
        return bookStore.existsById(isbn);
    }

    // ---- Member operations ----

    public void addMember(Member member) {
        memberStore.save(member.getId(), member);
    }

    public Optional<Member> findMemberById(String memberId) {
        return memberStore.findById(memberId);
    }

    public List<Member> getAllMembers() {
        return memberStore.findAll();
    }

    public boolean memberExists(String memberId) {
        return memberStore.existsById(memberId);
    }
}
