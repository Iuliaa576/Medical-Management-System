package validator;

import domain.Patient;
import repository.RepositoryException;

public class PatientValidator {

    public void validate(Patient patient) throws RepositoryException {
        if (patient.getId() == null || patient.getId().trim().isEmpty()) {
            throw new RepositoryException("Patient ID cannot be empty.");
        }

        if (patient.getName() == null || patient.getName().trim().isEmpty()) {
            throw new RepositoryException("Patient name cannot be empty.");
        }

        String email = patient.getEmail();
        if (email == null || email.trim().isEmpty()) {
            throw new RepositoryException("Email cannot be empty.");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new RepositoryException("Invalid email format.");
        }

        String phone = patient.getPhoneNumber();
        if (phone == null || phone.trim().isEmpty()) {
            throw new RepositoryException("Phone number cannot be empty.");
        }
        if (!phone.matches("\\d{10}")) {
            throw new RepositoryException("Phone number must contain exactly 10 digits.");
        }
    }
}
