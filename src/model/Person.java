package model;

/**
 * Abstract base class for every human actor in the library system.
 * Demonstrates ABSTRACTION (cannot be instantiated directly, forces
 * subclasses to define getRole()) and provides shared, encapsulated
 * state (id, name, email, phone) reused via INHERITANCE by Member and
 * Librarian.
 */
public abstract class Person {

    // Encapsulation: fields are private, exposed only through getters/setters
    private final String id;
    private String name;
    private String email;
    private String phone;

    protected Person(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Every concrete subclass must declare what role it plays in the
     * system. Abstract method -> forces subclasses to specialise.
     */
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("%-10s | %-20s | %-25s | %-15s | %s",
                id, name, email, phone, getRole());
    }
}
