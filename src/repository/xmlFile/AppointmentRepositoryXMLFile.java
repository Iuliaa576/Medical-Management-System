package repository.xmlFile;

import domain.Appointment;
import repository.FileRepository;
import repository.RepositoryException;

import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppointmentRepositoryXMLFile extends FileRepository<String, Appointment> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");

    public AppointmentRepositoryXMLFile(String FileName) throws RepositoryException {
        this.FileName = FileName;
        readFromFile();
    }

    @Override
    protected void readFromFile() throws RepositoryException {
        File file = new File(FileName);
        if (!file.exists() || file.length() == 0) {
            this.elements = new HashMap<>();
            return;
        }

        try {
            JAXBContext context = JAXBContext.newInstance(AppointmentWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            AppointmentWrapper wrapper = (AppointmentWrapper) unmarshaller.unmarshal(file);

            this.elements = new HashMap<>();
            for (AppointmentXML aXml : wrapper.getAppointments()) {
                LocalDateTime dateTime = LocalDateTime.parse(aXml.getDateTime(), FORMATTER);
                Appointment appointment = new Appointment(aXml.getId(), aXml.getPatientId(), dateTime);
                this.elements.put(appointment.getId(), appointment);
            }

        } catch (Exception e) {
            throw new RepositoryException("Error reading XML file: " + e.getMessage());
        }
    }

    @Override
    protected void writeToFile() {
        try {
            JAXBContext context = JAXBContext.newInstance(AppointmentWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            List<AppointmentXML> xmlList = new ArrayList<>();
            for (Appointment a : elements.values()) {
                xmlList.add(new AppointmentXML(
                        a.getId(),
                        a.getPatientId(),
                        a.getDateTime().format(FORMATTER)
                ));
            }

            AppointmentWrapper wrapper = new AppointmentWrapper(xmlList);
            marshaller.marshal(wrapper, new File(FileName));

        } catch (Exception e) {
            throw new RuntimeException("Error writing XML file: " + e.getMessage());
        }
    }

    // Wrapper for list
    @XmlRootElement(name = "appointments")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AppointmentWrapper {
        @XmlElement(name = "appointment")
        private List<AppointmentXML> appointments = new ArrayList<>();

        public AppointmentWrapper() {}
        public AppointmentWrapper(List<AppointmentXML> appointments) {
            this.appointments = appointments;
        }
        public List<AppointmentXML> getAppointments() { return appointments; }
    }

    // Simple XML structure
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class AppointmentXML {
        private String id;
        private String patientId;
        private String dateTime;

        public AppointmentXML() {}
        public AppointmentXML(String id, String patientId, String dateTime) {
            this.id = id;
            this.patientId = patientId;
            this.dateTime = dateTime;
        }

        public String getId() { return id; }
        public String getPatientId() { return patientId; }
        public String getDateTime() { return dateTime; }
    }
}
