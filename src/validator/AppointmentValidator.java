package validator;

import domain.Appointment;
import repository.RepositoryException;
import service.PatientService;

import java.time.LocalDateTime;

public class AppointmentValidator {

    private final PatientService patientService;

    // Pass PatientService in the constructor so we can check if patient exists
    public AppointmentValidator(PatientService patientService) {
        this.patientService = patientService;
    }

    public void validate(Appointment appointment) throws RepositoryException {
        if (appointment.getId() == null || appointment.getId().trim().isEmpty()) {
            throw new RepositoryException("Appointment ID cannot be empty.");
        }

        if (appointment.getPatientId() == null || appointment.getPatientId().trim().isEmpty()) {
            throw new RepositoryException("Patient ID cannot be empty.");
        }

        if (!patientService.exists(appointment.getPatientId())) {
            throw new RepositoryException("Invalid Patient ID. No patient found with this ID.");
        }

        LocalDateTime dateTime = appointment.getDateTime();
        if (dateTime == null) {
            throw new RepositoryException("Appointment date/time cannot be null.");
        }

        if (dateTime.isBefore(LocalDateTime.now())) {
            throw new RepositoryException("Appointment date/time must be in the future.");
        }
    }
}
