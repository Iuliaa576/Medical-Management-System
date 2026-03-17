package filter;

import domain.Appointment;

public class FilterAppointmentByPatientId implements AbstractFilter<Appointment> {

    String patientId;

    public FilterAppointmentByPatientId(String patientId) {
        this.patientId = patientId;
    }

    @Override
    public boolean accept(Appointment appointment) {
        return appointment.getPatientId().equals(this.patientId);
    }
}
