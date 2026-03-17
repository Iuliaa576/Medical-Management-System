package repository.binaryFile;

import domain.Patient;
import domain.Appointment;
import org.junit.jupiter.api.*;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class BinaryFileRepositoryTests {

    private File patientFile;
    private File appointmentFile;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    void setUp() throws Exception {
        patientFile = File.createTempFile("patients", ".bin");
        appointmentFile = File.createTempFile("appointments", ".bin");
        patientFile.deleteOnExit();
        appointmentFile.deleteOnExit();
    }

    @AfterEach
    void tearDown() {
        if (patientFile.exists()) patientFile.delete();
        if (appointmentFile.exists()) appointmentFile.delete();
    }

    @Test
    void testAddAndPersistPatients() throws Exception {
        PatientRepositoryBinaryFile repo = new PatientRepositoryBinaryFile(patientFile.getAbsolutePath());

        // Initially empty
        Assertions.assertEquals(0, countElements(repo.getAll()));

        // Add one patient (triggers writeToFile)
        repo.add("P01", new Patient("P01", "John", "john@mail.com", "0123456789"));
        Assertions.assertEquals(1, countElements(repo.getAll()));

        // Reopen repository (should read from binary file)
        PatientRepositoryBinaryFile reloaded = new PatientRepositoryBinaryFile(patientFile.getAbsolutePath());
        Assertions.assertEquals(1, countElements(reloaded.getAll()));

        Optional<Patient> p = reloaded.findById("P01");
        Assertions.assertTrue(p.isPresent());
        Assertions.assertEquals("John", p.get().getName());
        Assertions.assertEquals("john@mail.com", p.get().getEmail());
    }

    @Test
    void testDeleteAndPersistPatients() throws Exception {
        PatientRepositoryBinaryFile repo = new PatientRepositoryBinaryFile(patientFile.getAbsolutePath());
        repo.add("P01", new Patient("P01", "Alice", "alice@mail.com", "0711111111"));
        repo.add("P02", new Patient("P02", "Bob", "bob@mail.com", "0722222222"));
        Assertions.assertEquals(2, countElements(repo.getAll()));

        repo.delete("P01");
        Assertions.assertEquals(1, countElements(repo.getAll()));

        // Reload file — should only contain "P02"
        PatientRepositoryBinaryFile reloaded = new PatientRepositoryBinaryFile(patientFile.getAbsolutePath());
        Assertions.assertEquals(1, countElements(reloaded.getAll()));

        Optional<Patient> remaining = reloaded.findById("P02");
        Assertions.assertTrue(remaining.isPresent());
        Assertions.assertEquals("Bob", remaining.get().getName());
    }

    @Test
    void testAddAndPersistAppointments() throws Exception {
        AppointmentRepositoryBinaryFile repo = new AppointmentRepositoryBinaryFile(appointmentFile.getAbsolutePath());
        Assertions.assertEquals(0, countElements(repo.getAll()));

        repo.add("A01", new Appointment("A01", "P01", LocalDateTime.parse("2025-12-20 14:30", formatter)));
        Assertions.assertEquals(1, countElements(repo.getAll()));

        // Reload and check persistence
        AppointmentRepositoryBinaryFile reloaded = new AppointmentRepositoryBinaryFile(appointmentFile.getAbsolutePath());
        Assertions.assertEquals(1, countElements(reloaded.getAll()));

        Optional<Appointment> ap = reloaded.findById("A01");
        Assertions.assertTrue(ap.isPresent());
        Assertions.assertEquals("P01", ap.get().getPatientId());
        Assertions.assertEquals(LocalDateTime.parse("2025-12-20 14:30", formatter), ap.get().getDateTime());
    }

    @Test
    void testDeleteAndPersistAppointments() throws Exception {
        AppointmentRepositoryBinaryFile repo = new AppointmentRepositoryBinaryFile(appointmentFile.getAbsolutePath());
        repo.add("A01", new Appointment("A01", "P01", LocalDateTime.parse("2025-12-20 14:30", formatter)));
        repo.add("A02", new Appointment("A02", "P02", LocalDateTime.parse("2025-12-25 12:00", formatter)));
        Assertions.assertEquals(2, countElements(repo.getAll()));

        repo.delete("A02");
        Assertions.assertEquals(1, countElements(repo.getAll()));

        // Reload and check persistence
        AppointmentRepositoryBinaryFile reloaded = new AppointmentRepositoryBinaryFile(appointmentFile.getAbsolutePath());
        Assertions.assertEquals(1, countElements(reloaded.getAll()));

        Optional<Appointment> remaining = reloaded.findById("A01");
        Assertions.assertTrue(remaining.isPresent());
        Assertions.assertEquals("P01", remaining.get().getPatientId());
    }

    // Helper: count elements in an Iterable
    private static <T> long countElements(Iterable<T> iterable) {
        long count = 0;
        for (T ignored : iterable) count++;
        return count;
    }
}
