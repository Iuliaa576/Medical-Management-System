package filter;

import domain.Appointment;
import domain.Patient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FilterTests {

    private Patient p1, p2, p3;
    private Appointment a1, a2, a3;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    void setUp() {
        p1 = new Patient("P01", "James", "james@yahoo.com", "0793215003");
        p2 = new Patient("P02", "Alice", "alice@yahoo.com", "0739216032");
        p3 = new Patient("P03", "Jamie", "jamie@gmail.com", "0711111111");

        a1 = new Appointment("A01", "P01", LocalDateTime.parse("2025-12-20 14:30", formatter));
        a2 = new Appointment("A02", "P02", LocalDateTime.parse("2025-12-25 12:30", formatter));
        a3 = new Appointment("A03", "P01", LocalDateTime.parse("2025-12-20 14:30", formatter)); // same datetime as a1
    }

    // FilterPatientByName
    @Test
    void testFilterPatientByName() {
        var filter1 = new FilterPatientByName("James");
        Assertions.assertTrue(filter1.accept(p1));
        Assertions.assertFalse(filter1.accept(p2));

        var filter2 = new FilterPatientByName("jam");
        Assertions.assertTrue(filter2.accept(p1));  // "jam" in "James"
        Assertions.assertTrue(filter2.accept(p3));  // "jam" in "Jamie"
        Assertions.assertFalse(filter2.accept(p2)); // no match

        var filter3 = new FilterPatientByName("ALICE");
        Assertions.assertTrue(filter3.accept(p2));  // case-insensitive
    }

    // FilterPatientByEmail
    @Test
    void testFilterPatientByEmail() {
        var filter = new FilterPatientByEmail("alice@yahoo.com");
        Assertions.assertTrue(filter.accept(p2));
        Assertions.assertFalse(filter.accept(p1));
        Assertions.assertFalse(filter.accept(p3));

        // check case-sensitivity
        var filter2 = new FilterPatientByEmail("ALICE@YAHOO.COM");
        Assertions.assertFalse(filter2.accept(p2)); // should be false, exact match required
    }

    // FilterAppointmentByPatientId
    @Test
    void testFilterAppointmentByPatientId() {
        var filter = new FilterAppointmentByPatientId("P01");
        Assertions.assertTrue(filter.accept(a1)); // patientId matches
        Assertions.assertTrue(filter.accept(a3)); // same patientId
        Assertions.assertFalse(filter.accept(a2)); // different patientId
    }

    // FilterAppointmentByDateTime
    @Test
    void testFilterAppointmentByDateTime() {
        var filter = new FilterAppointmentByDateTime(LocalDateTime.parse("2025-12-20 14:30", formatter));
        Assertions.assertTrue(filter.accept(a1)); // exact match
        Assertions.assertTrue(filter.accept(a3)); // same datetime
        Assertions.assertFalse(filter.accept(a2)); // different datetime
    }
}