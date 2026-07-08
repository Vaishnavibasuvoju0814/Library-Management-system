package service;

/**
 * Reportable is implemented by any domain entity that can produce a
 * human-readable summary of itself. Book and Member both implement this
 * interface, and calling generateReport() on a Reportable reference will
 * invoke the correct implementation at runtime (runtime polymorphism).
 */
public interface Reportable {
    String generateReport();
}
