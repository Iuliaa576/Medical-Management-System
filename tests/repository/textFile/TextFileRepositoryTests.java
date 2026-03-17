package repository.textFile;

import domain.Patient;
import domain.Appointment;
import org.junit.jupiter.api.*;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TextFileRepositoryTests {

    private File patientFile;
    private File appointmentFile;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");

    @BeforeEach
    void setUp() throws Exception {
        patientFile = File.createTempFile("patients", ".txt");
        appointmentFile = File.createTempFile("appointments", ".txt");
        patientFile.deleteOnExit();
        appointmentFile.deleteOnExit();
    }

    @AfterEach
    void tearDown() {
        if (patientFile.exists()) patientFile.delete();
        if (appointmentFile.exists()) appointmentFile.delete();
    }

    @Test
    void testReadAndWritePatients() throws Exception {
        // Write one patient manually
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(patientFile))) {
            bw.write("P01,John,john@mail.com,0123456789\n");
        }

        PatientRepositoryTextFile repo = new PatientRepositoryTextFile(patientFile.getAbsolutePath());

        // verify reading
        Iterable<Patient> patients = repo.getAll();
        Assertions.assertEquals(1, countElements(patients));

        Patient first = patients.iterator().next();
        Assertions.assertEquals("John", first.getName());

        // Add a new patient (triggers writeToFile)
        repo.add("P02", new Patient("P02", "Alice", "alice@mail.com", "0999999999"));

        // Reload repository (reads from updated file)
        PatientRepositoryTextFile reloaded = new PatientRepositoryTextFile(patientFile.getAbsolutePath());
        Assertions.assertEquals(2, countElements(reloaded.getAll()));

        // Ensure "P02" is present
        boolean found = false;
        for (Patient p : reloaded.getAll()) {
            if (p.getId().equals("P02")) found = true;
        }
        Assertions.assertTrue(found);
    }

    @Test
    void testReadAndWriteAppointments() throws Exception {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(appointmentFile))) {
            bw.write("A01,P01,2025-12-10/14:30\n");
        }

        AppointmentRepositoryTextFile repo = new AppointmentRepositoryTextFile(appointmentFile.getAbsolutePath());

        // verify reading
        Iterable<Appointment> appointments = repo.getAll();
        Assertions.assertEquals(1, countElements(appointments));

        Appointment first = appointments.iterator().next();
        Assertions.assertEquals("P01", first.getPatientId());

        // add new appointment (triggers writeToFile)
        repo.add("A02", new Appointment("A02", "P02", LocalDateTime.parse("2025-12-11/16:30", formatter)));

        // reload and verify persisted
        AppointmentRepositoryTextFile reloaded = new AppointmentRepositoryTextFile(appointmentFile.getAbsolutePath());
        Assertions.assertEquals(2, countElements(reloaded.getAll()));

        boolean found = false;
        for (Appointment a : reloaded.getAll()) {
            if (a.getId().equals("A02")) found = true;
        }
        Assertions.assertTrue(found);
    }

    // Utility: counts items in any Iterable
    private static <T> long countElements(Iterable<T> iterable) {
        long count = 0;
        for (T ignored : iterable) count++;
        return count;
    }
}
