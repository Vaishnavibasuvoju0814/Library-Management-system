package model;

import service.Reportable;

import java.util.ArrayList;
import java.util.List;

/**
 * A library member who can borrow books. Demonstrates INHERITANCE
 * (extends Person), METHOD OVERRIDING (getRole, toString) and
 * interface implementation (Reportable).
 */
public class Member extends Person implements Reportable {

    private final List<String> issuedBookIsbns = new ArrayList<>();
    private double totalFinePaid;

    public Member(String id, String name, String email, String phone) {
        super(id, name, email, phone);
        this.totalFinePaid = 0.0;
    }

    @Override
    public String getRole() {
        return "MEMBER";
    }

    public List<String> getIssuedBookIsbns() {
        return issuedBookIsbns;
    }

    public void addIssuedBook(String isbn) {
        issuedBookIsbns.add(isbn);
    }

    public void removeIssuedBook(String isbn) {
        issuedBookIsbns.remove(isbn);
    }

    public double getTotalFinePaid() {
        return totalFinePaid;
    }

    public void addFine(double fine) {
        this.totalFinePaid += fine;
    }

    @Override
    public String generateReport() {
        return String.format("Member %s (%s) has %d book(s) issued, total fine paid: Rs.%.2f",
                getName(), getId(), issuedBookIsbns.size(), totalFinePaid);
    }

    /** Method overriding: customises the base Person representation. */
    @Override
    public String toString() {
        return super.toString() + String.format(" | Books Issued: %d | Fine Paid: Rs.%.2f",
                issuedBookIsbns.size(), totalFinePaid);
    }

    public String toFileLine() {
        return String.join("|", getId(), getName(), getEmail(), getPhone(),
                String.valueOf(totalFinePaid), String.join(",", issuedBookIsbns));
    }

    public static Member fromFileLine(String line) {
        String[] p = line.split("\\|", -1);
        Member member = new Member(p[0], p[1], p[2], p[3]);
        member.addFine(Double.parseDouble(p[4]));
        if (p.length > 5 && !p[5].isBlank()) {
            for (String isbn : p[5].split(",")) {
                member.addIssuedBook(isbn);
            }
        }
        return member;
    }
}
