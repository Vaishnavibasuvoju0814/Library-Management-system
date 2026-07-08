package model;

/**
 * Represents a staff member who administers the library. Kept lightweight
 * on purpose - its main job in this project is to demonstrate INHERITANCE
 * and METHOD OVERRIDING alongside Member, proving Person can be extended
 * in more than one way (a hallmark of good abstraction).
 */
public class Librarian extends Person {

    private String employeeId;

    public Librarian(String id, String name, String email, String phone, String employeeId) {
        super(id, name, email, phone);
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public String getRole() {
        return "LIBRARIAN";
    }

    @Override
    public String toString() {
        return super.toString() + " | Employee ID: " + employeeId;
    }
}
