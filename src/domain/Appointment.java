package domain;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Appointment implements Identifiable<String>, Serializable {

    private String id;
    private String patientId;
    private LocalDateTime dateTime;

    public Appointment(String id, String patientId, LocalDateTime dateTime) {
        this.id = id;
        this.patientId = patientId;
        this.dateTime = dateTime;
    }

    //Getters
    @Override
    public String getId() {
        return id;
    }

    public String getPatientId() {
        return patientId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    //Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", patientId=" + patientId +
                ", dateTime=" + dateTime +
                '}';
    }

}
