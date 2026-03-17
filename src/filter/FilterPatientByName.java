package filter;

import domain.Patient;

public class FilterPatientByName implements AbstractFilter<Patient> {
    private String name;

    public FilterPatientByName(String name) {
        this.name = name.toLowerCase();
    }

    @Override
    public boolean accept(Patient patient) {
        return patient.getName().toLowerCase().contains(this.name);
    }
}
