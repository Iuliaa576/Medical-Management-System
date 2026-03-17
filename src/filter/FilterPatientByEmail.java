package filter;

import domain.Patient;

public class FilterPatientByEmail implements AbstractFilter<Patient> {
    private String email;

    public FilterPatientByEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean accept(Patient patient) {
        return patient.getEmail().equals(this.email);
    }
}
