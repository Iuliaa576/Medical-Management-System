//package service;
//
//import domain.Appointment;
//import domain.Patient;
//import filter.AbstractFilter;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import repository.IRepository;
//import repository.RepositoryException;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//// Simple in-memory repository for testing purposes
//class InMemoryRepository<K, V> implements IRepository<K, V> {
//    private HashMap<K, V> data = new HashMap<>();
//
//    @Override
//    public void add(K id, V entity) throws RepositoryException {
//        if (data.containsKey(id)) {
//            throw new RepositoryException("Duplicate ID");
//        }
//        data.put(id, entity);
//    }
//
//    @Override
//    public Optional<V> delete(K id) throws RepositoryException {
//        if (!data.containsKey(id)) {
//            throw new RepositoryException("Entity not found");
//        }
//        data.remove(id);
//        return Optional.empty();
//    }
//
//    @Override
//    public void modify(K id, V newEntity) throws RepositoryException {
//        if (!data.containsKey(id)) {
//            throw new RepositoryException("Entity not found");
//        }
//        data.put(id, newEntity);
//    }
//
//    @Override
//    public Optional<V> findById(K id) throws RepositoryException {
//        V entity = data.get(id);
//        if (entity == null) {
//            throw new RepositoryException("Entity not found");
//        }
//        return Optional.of(entity);
//    }
//
//    @Override
//    public Iterable<V> getAll() {
//        return data.values();
//    }
//}
//
//public class ServiceTests {
//
//    private PatientService patientService;
//    private AppointmentService appointmentService;
//
//    @BeforeEach
//    void setup() throws RepositoryException {
//        IRepository<String, Patient> patientRepo = new InMemoryRepository<>();
//        IRepository<String, Appointment> appointmentRepo = new InMemoryRepository<>();
//
//        patientService = new PatientService(patientRepo, null);
//        appointmentService = new AppointmentService(appointmentRepo, patientService);
//        patientService.setAppointmentService(appointmentService);
//
//        // Valid patients that pass the validator
//        patientService.addPatient("P1", "Alice", "alice@mail.com", "0712345678");
//        patientService.addPatient("P2", "Bob", "bob@mail.com", "0722334455");
//    }
//
//    // PATIENT SERVICE TESTS
//
//    @Test
//    void testAddAndFindPatient() throws RepositoryException {
//        patientService.addPatient("P3", "Charlie", "charlie@mail.com", "0733445566");
//        Patient found = patientService.findPatientByID("P3");
//        assertEquals("Charlie", found.getName());
//        assertEquals("charlie@mail.com", found.getEmail());
//    }
//
//    @Test
//    void testUpdatePatient() throws RepositoryException {
//        patientService.updatePatientByID("P1", "Alicia", "alicia@mail.com", "0799999999");
//        Patient updated = patientService.findPatientByID("P1");
//        assertEquals("Alicia", updated.getName());
//        assertEquals("alicia@mail.com", updated.getEmail());
//        assertEquals("0799999999", updated.getPhoneNumber());
//    }
//
//    @Test
//    void testDeletePatient() {
//        assertThrows(RepositoryException.class, () -> patientService.deletePatientByID("P2"));
//        assertThrows(RepositoryException.class, () -> patientService.findPatientByID("P2"));
//    }
//
//    @Test
//    void testFilterPatientsByName() throws RepositoryException {
//        AbstractFilter<Patient> filter = p -> p.getName().toLowerCase().contains("a");
//        HashMap<String, Patient> result = patientService.filterPatients(filter);
//        assertTrue(result.containsKey("P1")); // Alice
//        assertFalse(result.containsKey("P2")); // Bob
//    }
//
//    // APPOINTMENT SERVICE TESTS
//
//    @Test
//    void testAddAndFindAppointment() throws RepositoryException {
//        LocalDateTime dateTime = LocalDateTime.now().plusMinutes(5);
//        appointmentService.addAppointment("A1", "P1", dateTime);
//        Appointment found = appointmentService.findAppointmentByID("A1");
//        assertEquals("P1", found.getPatientId());
//        assertEquals(dateTime, found.getDateTime());
//    }
//
//    @Test
//    void testUpdateAppointment() throws RepositoryException {
//        LocalDateTime dateTime = LocalDateTime.now().plusMinutes(5);
//        appointmentService.addAppointment("A2", "P1", dateTime);
//        LocalDateTime newTime = dateTime.plusDays(1);
//        appointmentService.updateAppointmentByID("A2", "P2", newTime);
//        Appointment updated = appointmentService.findAppointmentByID("A2");
//        assertEquals("P2", updated.getPatientId());
//        assertEquals(newTime, updated.getDateTime());
//    }
//
//    @Test
//    void testDeleteAppointment() throws RepositoryException {
//        LocalDateTime dateTime = LocalDateTime.now().plusMinutes(5);
//        appointmentService.addAppointment("A3", "P1", dateTime);
//
//        assertThrows(RepositoryException.class, () -> appointmentService.deleteAppointmentByID("A3"));
//        assertThrows(RepositoryException.class, () -> appointmentService.findAppointmentByID("A3"));
//    }
//
//    @Test
//    void testFilterAppointmentsByPatientId() throws RepositoryException {
//        LocalDateTime dateTime1 = LocalDateTime.now().plusMinutes(5);
//        LocalDateTime dateTime2 = dateTime1.plusHours(2);
//        appointmentService.addAppointment("A4", "P1", dateTime1);
//        appointmentService.addAppointment("A5", "P2", dateTime2);
//
//        AbstractFilter<Appointment> filter = a -> a.getPatientId().equals("P1");
//        HashMap<String, Appointment> filtered = appointmentService.filterAppointments(filter);
//
//        List<String> ids = filtered.values().stream().map(Appointment::getId).collect(Collectors.toList());
//        assertTrue(ids.contains("A4"));
//        assertFalse(ids.contains("A5"));
//    }
//
//}
