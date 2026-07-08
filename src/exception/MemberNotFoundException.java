package exception;

/**
 * Thrown when a member lookup by member ID fails.
 */
public class MemberNotFoundException extends Exception {

    public MemberNotFoundException(String memberId) {
        super("No member found with ID: " + memberId);
    }
}
