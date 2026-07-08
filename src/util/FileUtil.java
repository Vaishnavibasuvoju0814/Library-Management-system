package util;

import model.Book;
import model.Member;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles persistence of Book and Member data to plain text files
 * (pipe-delimited), demonstrating FILE HANDLING with java.nio.file and
 * try-with-resources for safe stream closing.
 */
public final class FileUtil {

    private static final Path DATA_DIR = Path.of("data");
    private static final Path BOOKS_FILE = DATA_DIR.resolve("books.txt");
    private static final Path MEMBERS_FILE = DATA_DIR.resolve("members.txt");

    private FileUtil() {
        // utility class - no instances
    }

    public static void saveBooks(List<Book> books) throws IOException {
        ensureDataDir();
        try (BufferedWriter writer = Files.newBufferedWriter(BOOKS_FILE)) {
            for (Book book : books) {
                writer.write(book.toFileLine());
                writer.newLine();
            }
        }
    }

    public static List<Book> loadBooks() throws IOException {
        List<Book> books = new ArrayList<>();
        if (!Files.exists(BOOKS_FILE)) {
            return books;
        }
        try (BufferedReader reader = Files.newBufferedReader(BOOKS_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    books.add(Book.fromFileLine(line));
                }
            }
        }
        return books;
    }

    public static void saveMembers(List<Member> members) throws IOException {
        ensureDataDir();
        try (BufferedWriter writer = Files.newBufferedWriter(MEMBERS_FILE)) {
            for (Member member : members) {
                writer.write(member.toFileLine());
                writer.newLine();
            }
        }
    }

    public static List<Member> loadMembers() throws IOException {
        List<Member> members = new ArrayList<>();
        if (!Files.exists(MEMBERS_FILE)) {
            return members;
        }
        try (BufferedReader reader = Files.newBufferedReader(MEMBERS_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    members.add(Member.fromFileLine(line));
                }
            }
        }
        return members;
    }

    private static void ensureDataDir() throws IOException {
        if (!Files.exists(DATA_DIR)) {
            Files.createDirectories(DATA_DIR);
        }
    }
}
