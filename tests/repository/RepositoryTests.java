package repository;

import domain.Appointment;
import domain.Patient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class RepositoryTests {

    private MemoryRepository<String, Patient> patientRepository;
    private MemoryRepository<String, Appointment> appointmentRepository;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @BeforeEach
    void setUpRepo() {
        patientRepository = new MemoryRepository<>();
        appointmentRepository = new MemoryRepository<>();
        try {
            patientRepository.add("P01", new Patient("P01", "James", "james@yahoo.com", "0793215003"));
            patientRepository.add("P02", new Patient("P02", "Alice", "alice@yahoo.com", "0739216032"));

            appointmentRepository.add("A01", new Appointment("A01", "P01", LocalDateTime.parse("2025-12-20 14:30", formatter)));
            appointmentRepository.add("A02", new Appointment("A02", "P02", LocalDateTime.parse("2025-12-25 12:30", formatter)));
        } catch (RepositoryException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAdd() {
        // PATIENTS
        // case 1: add does NOT throw
        try {
            patientRepository.add("P03", new Patient("P03", "Jim", "jim@yahoo.com", "0729145032"));
        }
        catch (RepositoryException e) {
            assert false;
        }

        // assert repo.getSize() == 3;
        Assertions.assertEquals(3, countElements(patientRepository.getAll()));

        // case 2: add throws exception
        try {
            patientRepository.add("P03", new Patient("P03", "Jim", "jim@yahoo.com", "0729145032"));
            assert false;
        }
        catch (RepositoryException e) {
            assert true;
        }

        Assertions.assertEquals(3, countElements(patientRepository.getAll()));

        // APPOINTMENTS
        // case 1: add does NOT throw
        try {
            appointmentRepository.add("A03", new Appointment("A03", "P03", LocalDateTime.parse("2025-12-27 10:30", formatter)));
        }
        catch (RepositoryException e) {
            assert false;
        }

        // assert repo.getSize() == 3;
        Assertions.assertEquals(3, countElements(appointmentRepository.getAll()));

        // case 2: add throws exception
        try {
            appointmentRepository.add("A03", new Appointment("A03", "P03", LocalDateTime.parse("2025-12-27 10:30", formatter)));
            assert false;
        }
        catch (RepositoryException e) {
            assert true;
        }

        Assertions.assertEquals(3, countElements(appointmentRepository.getAll()));
    }

    @Test
    void testDelete() {
        // PATIENTS
        // case 1: delete existing
        try {
            patientRepository.delete("P01");
        } catch (RepositoryException e) {
            assert false;
        }

        Assertions.assertEquals(1, countElements(patientRepository.getAll()));

        // case 2: delete non-existing
        try {
            Optional<Patient> result = patientRepository.delete("PX");
            Assertions.assertTrue(result.isEmpty());
        } catch (RepositoryException e) {
            assert false;
        }

        // still 1 element left
        Assertions.assertEquals(1, countElements(patientRepository.getAll()));

        // APPOINTMENTS
        // case 1: delete existing
        try {
            Optional<Appointment> deleted = appointmentRepository.delete("A01");
            Assertions.assertTrue(deleted.isPresent());
        } catch (RepositoryException e) {
            assert false;
        }

        Assertions.assertEquals(1, countElements(appointmentRepository.getAll()));

        // case 2: delete non-existing
        try {
            Optional<Appointment> result = appointmentRepository.delete("AX");
            // for a non-existing id, delete() should return Optional.empty()
            Assertions.assertTrue(result.isEmpty());
        } catch (RepositoryException e) {
            assert false; // should NOT throw
        }

        // still 1 element left
        Assertions.assertEquals(1, countElements(appointmentRepository.getAll()));

    }

    @Test
    void testModify() {
        // PATIENTS
        Patient modifiedPatient = new Patient("P02", "Alice B.", "alice_b@yahoo.com", "0700000000");

        // case 1: modify existing
        try {
            patientRepository.modify("P02", modifiedPatient);
        } catch (RepositoryException e) {
            assert false;
        }

        try {
            Optional<Patient> foundPatient = patientRepository.findById("P02");
            Assertions.assertTrue(foundPatient.isPresent());
            Assertions.assertEquals("Alice B.", foundPatient.get().getName());
            Assertions.assertEquals("alice_b@yahoo.com", foundPatient.get().getEmail());
        } catch (RepositoryException e) {
            assert false;
        }

        // case 2: modify non-existing
        try {
            patientRepository.modify("PX", new Patient("PX", "Ghost", "ghost@mail.com", "000"));
            assert false;
        } catch (RepositoryException e) {
            assert true;
        }

        // APPOINTMENTS
        Appointment modifiedAppointment = new Appointment("A02", "P01", LocalDateTime.parse("2025-12-30 13:30", formatter));

        // case 1: modify existing
        try {
            appointmentRepository.modify("A02", modifiedAppointment);
        } catch (RepositoryException e) {
            assert false;
        }

        try {
            Optional<Appointment> foundAppointment = appointmentRepository.findById("A02");
            Assertions.assertTrue(foundAppointment.isPresent());
            Assertions.assertEquals("P01", foundAppointment.get().getPatientId());
            Assertions.assertEquals(LocalDateTime.parse("2025-12-30 13:30", formatter), foundAppointment.get().getDateTime());
        } catch (RepositoryException e) {
            assert false;
        }

        // case 2: modify non-existing
        try {
            appointmentRepository.modify("AX", new Appointment("AX", "P01", LocalDateTime.parse("2025-12-22 17:30", formatter)));
            assert false;
        } catch (RepositoryException e) {
            assert true;
        }
    }

    @Test
    void testFindById() {
        // PATIENTS
        // case 1: find existing
        try {
            Optional<Patient> foundPatient = patientRepository.findById("P01");
            Assertions.assertTrue(foundPatient.isPresent());
            Assertions.assertEquals("James", foundPatient.get().getName());
        } catch (RepositoryException e) {
            assert false;
        }

        // case 2: find non-existing
        try {
            Optional<Patient> result = patientRepository.findById("PX");
            Assertions.assertTrue(result.isEmpty());
        } catch (RepositoryException e) {
            assert false;
        }

        // APPOINTMENTS
        // case 1: find existing
        try {
            Optional<Appointment> foundAppointment = appointmentRepository.findById("A01");
            Assertions.assertTrue(foundAppointment.isPresent());
            Assertions.assertEquals("P01", foundAppointment.get().getPatientId());
        } catch (RepositoryException e) {
            assert false;
        }

        // case 2: find non-existing
        try {
            Optional<Appointment> result = appointmentRepository.findById("AX");
            Assertions.assertTrue(result.isEmpty());
        } catch (RepositoryException e) {
            assert false; // should NOT throw
        }
    }

    @Test
    void testGetAll() {
        // PATIENTS
        Iterable<Patient> patients = patientRepository.getAll();
        Assertions.assertEquals(2, countElements(patients));

        // add new element and check again
        try {
            patientRepository.add("P03", new Patient("P03", "Jim", "jim@yahoo.com", "0729145032"));
        } catch (RepositoryException e) {
            assert false;
        }

        Assertions.assertEquals(3, countElements(patientRepository.getAll()));

        // APPOINTMENTS
        Iterable<Appointment> appointments = appointmentRepository.getAll();
        Assertions.assertEquals(2, countElements(appointments));

        // add new element and check again
        try {
            appointmentRepository.add("A03", new Appointment("A03", "P02", LocalDateTime.parse("2025-11-28 15:00", formatter)));
        } catch (RepositoryException e) {
            assert false;
        }

        Assertions.assertEquals(3, countElements(appointmentRepository.getAll()));
    }

    // Helper method for counting elements in map
    private <T> long countElements(Iterable<T> iterable) {
        long count = 0;
        for (T ignored : iterable) {
            count++;
        }
        return count;
    }
}
