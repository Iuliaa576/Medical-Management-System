package filter;

import domain.Appointment;

import java.time.LocalDateTime;

public class FilterAppointmentByDateTime implements AbstractFilter<Appointment> {

    private LocalDateTime dateTime;

    public FilterAppointmentByDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public boolean accept(Appointment appointment) {
        return appointment.getDateTime().equals(this.dateTime);
    }
}
