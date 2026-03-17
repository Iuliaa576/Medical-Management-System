package service;

import domain.Appointment;
import domain.Patient;
import filter.AbstractFilter;
import repository.FilteredRepository;
import repository.IRepository;
import repository.RepositoryException;
import undo.*;
import validator.PatientValidator;

import java.util.HashMap;
import java.util.Optional;
import java.util.stream.StreamSupport;

public class PatientService {

    private IRepository<String, Patient> patientRepository;
    private PatientValidator validator = new PatientValidator();

    private AppointmentService appointmentService;
    private UndoRedoManager undoRedoManager;

    public PatientService(IRepository<String, Patient> patientRepository,
                          AppointmentService appointmentService,
                          UndoRedoManager undoRedoManager) {
        this.patientRepository = patientRepository;
        this.validator = new PatientValidator();
        this.appointmentService = appointmentService;
        this.undoRedoManager = undoRedoManager;
    }

    public boolean exists(String patientId) {
        return StreamSupport.stream(patientRepository.getAll().spliterator(), false)
                .anyMatch(p -> p.getId().equals(patientId));
    }

    public void addPatient(String id, String name, String email, String phoneNumber, int age) throws RepositoryException {
        Patient patient = new Patient(id, name, email, phoneNumber, age);
        validator.validate(patient);
        patientRepository.add(id, patient);

        undoRedoManager.record(new ActionAdd<>(patientRepository, id, patient));
    }

    public HashMap<String, Patient> getAll() {
        HashMap<String, Patient> result = new HashMap<>();
        patientRepository.getAll().forEach(p -> result.put(p.getId(), p));
        return result;
    }

    public HashMap<String, Patient> filterPatients(AbstractFilter<Patient> filter)
            throws RepositoryException {

        FilteredRepository<String, Patient> filteredRepo = new FilteredRepository<>(filter);

        // Copy all patients into the filtered repository (using lambda)
        patientRepository.getAll().forEach(p -> {
            try {
                filteredRepo.add(p.getId(), p);
            } catch (RepositoryException ignored) {}
        });

        // Collect filtered values into a HashMap (using lambda)
        HashMap<String, Patient> result = new HashMap<>();
        filteredRepo.getAll().forEach(p -> result.put(p.getId(), p));

        return result;
    }

    public Patient findPatientByID(String id) throws RepositoryException {
        return patientRepository.findById(id).orElse(null);
    }

    public void updatePatientByID(String id, String name, String email, String phoneNumber, int age) throws RepositoryException {
        Patient existing = patientRepository.findById(id)
                .orElseThrow(() -> new RepositoryException("Patient with id " + id + " doesn't exist."));

        Patient oldCopy = new Patient(existing.getId(), existing.getName(), existing.getEmail(), existing.getPhoneNumber(), existing.getAge());
        Patient newCopy = new Patient(id, name, email, phoneNumber, age);

        validator.validate(newCopy);
        patientRepository.modify(id, newCopy);

        undoRedoManager.record(new ActionUpdate<>(patientRepository, id, oldCopy, newCopy));
    }

    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    public void deletePatientByID(String id) throws RepositoryException {
        // 1) read patient + related appointments first
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RepositoryException("Patient with id " + id + " doesn't exist."));

        java.util.List<Appointment> related = appointmentService.getAppointmentsByPatientId(id);

        // 2) perform deletes
        patientRepository.delete(id);
        for (Appointment a : related) {
            appointmentService.deleteAppointmentRaw(a.getId());
        }

        // 3) build ONE composite action
        CompositeAction cascade = new CompositeAction();

        // redo should delete again -> so action type is "remove" with stored deleted objects
        cascade.add(new ActionRemove<>(patientRepository, id, patient));

        for (Appointment a : related) {
            cascade.add(new ActionRemove<>(appointmentService.getRepository(), a.getId(), a));
        }

        undoRedoManager.record(cascade);
    }

    public void undo() throws Exception { undoRedoManager.undo(); }
    public void redo() throws Exception { undoRedoManager.redo(); }
}
