package service;

import domain.Appointment;
import filter.AbstractFilter;
import repository.FilteredRepository;
import repository.IRepository;
import repository.RepositoryException;
import undo.ActionAdd;
import undo.ActionRemove;
import undo.ActionUpdate;
import undo.UndoRedoManager;
import validator.AppointmentValidator;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Optional;

public class AppointmentService {

    private IRepository<String, Appointment> appointmentRepository;
    private PatientService patientService;
    private AppointmentValidator validator;
    private UndoRedoManager undoRedoManager;

    public AppointmentService(IRepository<String, Appointment> appointmentRepository, PatientService patientService,
                              UndoRedoManager undoRedoManager) {
        this.appointmentRepository = appointmentRepository;
        this.patientService = patientService;
        this.undoRedoManager = undoRedoManager;
        this.validator = new AppointmentValidator(patientService);
    }

    public void addAppointment(String id, String patientId, LocalDateTime dateTime) throws RepositoryException {
        Appointment appointment = new Appointment(id, patientId, dateTime);
        validator.validate(appointment);
        appointmentRepository.add(id, appointment);

        undoRedoManager.record(new ActionAdd<>(appointmentRepository, id, appointment));
    }

    public HashMap<String, Appointment> getAll() {
        HashMap<String, Appointment> result = new HashMap<>();
        appointmentRepository.getAll().forEach(a -> result.put(a.getId(), a));
        return result;
    }

    public void deleteAppointmentByID(String id) throws RepositoryException {
        Appointment deleted = appointmentRepository.delete(id)
                .orElseThrow(() -> new RepositoryException("Appointment with id " + id + " doesn't exist."));

        undoRedoManager.record(new ActionRemove<>(appointmentRepository, id, deleted));
    }

    public HashMap<String, Appointment> filterAppointments(AbstractFilter<Appointment> filter)
            throws RepositoryException {

        FilteredRepository<String, Appointment> filteredRepo = new FilteredRepository<>(filter);

        appointmentRepository.getAll().forEach(a -> {
            try {
                filteredRepo.add(a.getId(), a);
            } catch (RepositoryException ignored) {}
        });

        HashMap<String, Appointment> result = new HashMap<>();
        filteredRepo.getAll().forEach(a -> result.put(a.getId(), a));

        return result;
    }
    
    public Appointment findAppointmentByID(String id) throws RepositoryException {
        return appointmentRepository.findById(id).orElse(null);
    }

    public void updateAppointmentByID(String id, String patientId, LocalDateTime dateTime) throws RepositoryException {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RepositoryException("Appointment with id " + id + " doesn't exist."));

        Appointment oldCopy = new Appointment(existing.getId(), existing.getPatientId(), existing.getDateTime());
        Appointment newCopy = new Appointment(id, patientId, dateTime);

        validator.validate(newCopy);
        appointmentRepository.modify(id, newCopy);

        undoRedoManager.record(new ActionUpdate<>(appointmentRepository, id, oldCopy, newCopy));
    }

    public void deleteAppointmentsByPatientId(String patientId) throws RepositoryException {
        for (Appointment appointment : appointmentRepository.getAll()) {
            if (appointment.getPatientId().equals(patientId)) {
                appointmentRepository.delete(appointment.getId());
            }
        }
    }

    public java.util.List<Appointment> getAppointmentsByPatientId(String patientId) {
        java.util.List<Appointment> result = new java.util.ArrayList<>();
        for (Appointment a : appointmentRepository.getAll()) {
            if (a.getPatientId().equals(patientId)) result.add(a);
        }
        return result;
    }

    // AppointmentService
    void deleteAppointmentRaw(String id) throws RepositoryException {
        appointmentRepository.delete(id)
                .orElseThrow(() -> new RepositoryException("Appointment with id " + id + " doesn't exist."));
    }

    // in AppointmentService
    IRepository<String, Appointment> getRepository() {
        return appointmentRepository;
    }

    public void undo() throws Exception { undoRedoManager.undo(); }
    public void redo() throws Exception { undoRedoManager.redo(); }
}
