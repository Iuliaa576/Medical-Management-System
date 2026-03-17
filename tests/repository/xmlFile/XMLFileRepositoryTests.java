package repository.xmlFile;

import domain.Patient;
import domain.Appointment;
import org.junit.jupiter.api.*;
import repository.RepositoryException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class XMLFileRepositoryTests {

    private static final String PATIENT_FILE = "test_patients.xml";
    private static final String APPOINTMENT_FILE = "test_appointments.xml";

    private PatientRepositoryXMLFile patientRepo;
    private AppointmentRepositoryXMLFile appointmentRepo;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");

    // Helper: convert Iterable to List
    private static <T> List<T> toList(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        for (T item : iterable) list.add(item);
        return list;
    }

    @BeforeEach
    void setUp() throws RepositoryException {
        new File(PATIENT_FILE).delete();
        new File(APPOINTMENT_FILE).delete();

        patientRepo = new PatientRepositoryXMLFile(PATIENT_FILE);
        appointmentRepo = new AppointmentRepositoryXMLFile(APPOINTMENT_FILE);
    }

    @AfterEach
    void tearDown() {
        new File(PATIENT_FILE).delete();
        new File(APPOINTMENT_FILE).delete();
    }

    // PATIENT TESTS

    @Test
    @Order(1)
    void testPatientAddAndPersistence() throws RepositoryException {
        Patient p = new Patient("P01", "Alice", "alice@mail.com", "0711111111");
        patientRepo.add(p.getId(), p);

        File f = new File(PATIENT_FILE);
        assertTrue(f.exists() && f.length() > 0);

        PatientRepositoryXMLFile reloaded = new PatientRepositoryXMLFile(PATIENT_FILE);
        List<Patient> patients = toList(reloaded.getAll());
        assertEquals(1, patients.size());
        assertEquals("Alice", patients.get(0).getName());
    }

    @Test
    @Order(2)
    void testPatientModifyAndPersistence() throws RepositoryException {
        patientRepo.add("P01", new Patient("P01", "Alice", "a@mail.com", "0711111111"));

        Patient updated = new Patient("P01", "Alicia", "new@mail.com", "0722222222");
        patientRepo.modify("P01", updated);

        assertEquals("Alicia", patientRepo.findById("P01").get().getName());

        PatientRepositoryXMLFile reloaded = new PatientRepositoryXMLFile(PATIENT_FILE);
        assertEquals("Alicia", reloaded.findById("P01").get().getName());
        assertEquals("new@mail.com", reloaded.findById("P01").get().getEmail());
    }

    @Test
    @Order(3)
    void testPatientDeleteAndPersistence() throws RepositoryException {
        patientRepo.add("P01", new Patient("P01", "Alice", "a@mail.com", "0711111111"));
        patientRepo.add("P02", new Patient("P02", "Bob", "b@mail.com", "0722222222"));

        assertEquals(2, toList(patientRepo.getAll()).size());

        patientRepo.delete("P01");

        List<Patient> patientsAfter = toList(patientRepo.getAll());
        assertEquals(1, patientsAfter.size());

        PatientRepositoryXMLFile reloaded = new PatientRepositoryXMLFile(PATIENT_FILE);
        List<Patient> loaded = toList(reloaded.getAll());
        assertEquals(1, loaded.size());
        assertEquals("Bob", loaded.get(0).getName());
    }

    @Test
    @Order(4)
    void testPatientReadFromEmptyFile() throws RepositoryException, IOException {
        File f = new File(PATIENT_FILE);
        f.createNewFile();

        PatientRepositoryXMLFile emptyRepo = new PatientRepositoryXMLFile(PATIENT_FILE);
        List<Patient> patients = toList(emptyRepo.getAll());
        assertTrue(patients.isEmpty());
    }

    @Test
    @Order(5)
    void testPatientInvalidFileThrowsException() {
        assertThrows(RepositoryException.class, () -> {
            try (FileWriter writer = new FileWriter(PATIENT_FILE)) {
                writer.write("<invalid><broken></xml>");
            }
            new PatientRepositoryXMLFile(PATIENT_FILE);
        });
    }

    // APPOINTMENT TESTS

    @Test
    @Order(6)
    void testAppointmentAddAndPersistence() throws RepositoryException {
        Appointment a = new Appointment("A01", "P01",
                LocalDateTime.parse("2025-12-05/11:00", FORMATTER));
        appointmentRepo.add(a.getId(), a);

        File f = new File(APPOINTMENT_FILE);
        assertTrue(f.exists() && f.length() > 0);

        AppointmentRepositoryXMLFile reloaded = new AppointmentRepositoryXMLFile(APPOINTMENT_FILE);
        List<Appointment> appointments = toList(reloaded.getAll());
        assertEquals(1, appointments.size());
        assertEquals(LocalDateTime.parse("2025-12-05/11:00", FORMATTER),
                appointments.get(0).getDateTime());
    }

    @Test
    @Order(7)
    void testAppointmentModifyAndPersistence() throws RepositoryException {
        Appointment a = new Appointment("A01", "P01",
                LocalDateTime.parse("2025-12-05/11:00", FORMATTER));
        appointmentRepo.add(a.getId(), a);

        Appointment updated = new Appointment("A01", "P01",
                LocalDateTime.parse("2025-12-10/12:00", FORMATTER));
        appointmentRepo.modify("A01", updated);

        assertEquals(LocalDateTime.parse("2025-12-10/12:00", FORMATTER),
                appointmentRepo.findById("A01").get().getDateTime());

        AppointmentRepositoryXMLFile reloaded = new AppointmentRepositoryXMLFile(APPOINTMENT_FILE);
        assertEquals(LocalDateTime.parse("2025-12-10/12:00", FORMATTER),
                reloaded.findById("A01").get().getDateTime());
    }

    @Test
    @Order(8)
    void testAppointmentDeleteAndPersistence() throws RepositoryException {
        appointmentRepo.add("A01", new Appointment("A01", "P01",
                LocalDateTime.parse("2025-12-05/11:00", FORMATTER)));
        appointmentRepo.add("A02", new Appointment("A02", "P02",
                LocalDateTime.parse("2025-12-06/13:00", FORMATTER)));

        assertEquals(2, toList(appointmentRepo.getAll()).size());

        appointmentRepo.delete("A01");
        assertEquals(1, toList(appointmentRepo.getAll()).size());

        AppointmentRepositoryXMLFile reloaded = new AppointmentRepositoryXMLFile(APPOINTMENT_FILE);
        List<Appointment> loaded = toList(reloaded.getAll());
        assertEquals(1, loaded.size());
        assertEquals("A02", loaded.get(0).getId());
    }

    @Test
    @Order(9)
    void testAppointmentReadFromEmptyFile() throws RepositoryException, IOException {
        File f = new File(APPOINTMENT_FILE);
        f.createNewFile();

        AppointmentRepositoryXMLFile emptyRepo = new AppointmentRepositoryXMLFile(APPOINTMENT_FILE);
        List<Appointment> appointments = toList(emptyRepo.getAll());
        assertTrue(appointments.isEmpty());
    }

    @Test
    @Order(10)
    void testAppointmentInvalidFileThrowsException() {
        assertThrows(RepositoryException.class, () -> {
            try (FileWriter writer = new FileWriter(APPOINTMENT_FILE)) {
                writer.write("<appointments><appointment><broken></appointments>");
            }
            new AppointmentRepositoryXMLFile(APPOINTMENT_FILE);
        });
    }
}
