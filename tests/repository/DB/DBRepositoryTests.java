package repository.DB;

import domain.Appointment;
import domain.Patient;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class DBRepositoryTests {

    // Shared in-memory SQLite database URL
    private static final String DB_URL = "jdbc:sqlite:file:memdb1?mode=memory&cache=shared";

    private Connection connection;
    private PatientRepositoryDB patientRepo;
    private AppointmentRepositoryDB appointmentRepo;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    void setUp() throws Exception {
        // Keep the connection open for all tests (required for shared in-memory)
        connection = DriverManager.getConnection(DB_URL);
        createTables();

        // Instantiate repositories — each opens its own connection to the same shared in-memory DB
        patientRepo = new PatientRepositoryDB(DB_URL);
        appointmentRepo = new AppointmentRepositoryDB(DB_URL);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS patient (" +
                    "id TEXT PRIMARY KEY, " +
                    "name TEXT, " +
                    "email TEXT, " +
                    "phoneNumber TEXT)");

            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS appointment (" +
                    "id TEXT PRIMARY KEY, " +
                    "patientId TEXT, " +
                    "dateTime TEXT)");
        }
    }

    // PATIENT TESTS

    @Test
    void testAddAndFindPatient() throws Exception {
        Patient p1 = new Patient("P01", "Alice", "alice@mail.com", "0700000000");
        patientRepo.add("P01", p1);

        Optional<Patient> found = patientRepo.findById("P01");
        assertNotNull(found);
        Assertions.assertTrue(found.isPresent());
        assertEquals("Alice", found.get().getName());
        assertEquals("alice@mail.com", found.get().getEmail());
        assertEquals("0700000000", found.get().getPhoneNumber());
    }

    @Test
    void testModifyAndDeletePatient() throws Exception {
        Patient p = new Patient("P02", "Bob", "bob@mail.com", "0711111111");
        patientRepo.add("P02", p);

        // Modify existing patient
        Patient modified = new Patient("P02", "Bob Jr.", "bobjr@mail.com", "0722222222");
        patientRepo.modify("P02", modified);

        Optional<Patient> found = patientRepo.findById("P02");
        Assertions.assertTrue(found.isPresent());
        assertEquals("Bob Jr.", found.get().getName());
        assertEquals("bobjr@mail.com", found.get().getEmail());
        assertEquals("0722222222", found.get().getPhoneNumber());

        // Delete
        patientRepo.delete("P02");
        Optional<Patient> deletedPatient = patientRepo.findById("P02");
        assertTrue(deletedPatient.isEmpty());
    }

    @Test
    void testGetAllPatients() throws Exception {
        patientRepo.add("P01", new Patient("P01", "Alice", "alice@mail.com", "0700000000"));
        patientRepo.add("P02", new Patient("P02", "Bob", "bob@mail.com", "0711111111"));
        patientRepo.add("P03", new Patient("P03", "Charlie", "charlie@mail.com", "0722222222"));

        int count = 0;
        for (Patient ignored : patientRepo.getAll()) count++;
        assertEquals(3, count);
    }

    // APPOINTMENT TESTS

    @Test
    void testAddAndFindAppointment() throws Exception {
        Appointment a1 = new Appointment("A01", "P01", LocalDateTime.parse("2025-12-20 14:30", formatter));
        appointmentRepo.add("A01", a1);

        Optional<Appointment> found = appointmentRepo.findById("A01");
        assertNotNull(found);
        Assertions.assertTrue(found.isPresent());
        assertEquals("P01", found.get().getPatientId());
        assertEquals(LocalDateTime.parse("2025-12-20 14:30", formatter), found.get().getDateTime());
    }

    @Test
    void testModifyAndDeleteAppointment() throws Exception {
        Appointment a = new Appointment("A02", "P02", LocalDateTime.parse("2025-12-22 09:00", formatter));
        appointmentRepo.add("A02", a);

        // Modify existing appointment
        Appointment modified = new Appointment("A02", "P02", LocalDateTime.parse("2025-12-23 10:00", formatter));
        appointmentRepo.modify("A02", modified);

        Optional<Appointment> found = appointmentRepo.findById("A02");
        Assertions.assertTrue(found.isPresent());
        assertEquals(LocalDateTime.parse("2025-12-23 10:00", formatter), found.get().getDateTime());

        // Delete
        appointmentRepo.delete("A02");
        Optional<Appointment> result = appointmentRepo.findById("A02");
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAllAppointments() throws Exception {
        appointmentRepo.add("A01", new Appointment("A01", "P01", LocalDateTime.parse("2025-12-20 14:30", formatter)));
        appointmentRepo.add("A02", new Appointment("A02", "P02", LocalDateTime.parse("2025-12-21 15:00", formatter)));
        appointmentRepo.add("A03", new Appointment("A03", "P03", LocalDateTime.parse("2025-12-22 16:00", formatter)));

        int count = 0;
        for (Appointment ignored : appointmentRepo.getAll()) count++;
        assertEquals(3, count);
    }
}
