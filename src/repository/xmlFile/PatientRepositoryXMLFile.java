package repository.xmlFile;

import domain.Patient;
import repository.FileRepository;
import repository.RepositoryException;

import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PatientRepositoryXMLFile extends FileRepository<String, Patient> {

    public PatientRepositoryXMLFile(String FileName) throws RepositoryException {
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
            JAXBContext context = JAXBContext.newInstance(PatientWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            PatientWrapper wrapper = (PatientWrapper) unmarshaller.unmarshal(file);

            this.elements = new HashMap<>();
            for (PatientXML pXml : wrapper.getPatients()) {
                Patient patient = new Patient(pXml.getId(), pXml.getName(), pXml.getEmail(), pXml.getPhoneNumber());
                this.elements.put(patient.getId(), patient);
            }

        } catch (Exception e) {
            throw new RepositoryException("Error reading XML file: " + e.getMessage());
        }
    }

    @Override
    protected void writeToFile() {
        try {
            JAXBContext context = JAXBContext.newInstance(PatientWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            List<PatientXML> xmlList = new ArrayList<>();
            for (Patient p : elements.values()) {
                xmlList.add(new PatientXML(p.getId(), p.getName(), p.getEmail(), p.getPhoneNumber()));
            }

            PatientWrapper wrapper = new PatientWrapper(xmlList);
            marshaller.marshal(wrapper, new File(FileName));

        } catch (Exception e) {
            throw new RuntimeException("Error writing XML file: " + e.getMessage());
        }
    }

    // Wrapper for patient list
    @XmlRootElement(name = "patients")
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PatientWrapper {
        @XmlElement(name = "patient")
        private List<PatientXML> patients = new ArrayList<>();

        public PatientWrapper() {}
        public PatientWrapper(List<PatientXML> patients) {
            this.patients = patients;
        }

        public List<PatientXML> getPatients() { return patients; }
    }

    // XML class
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class PatientXML {
        private String id;
        private String name;
        private String email;
        private String phoneNumber;

        public PatientXML() {}
        public PatientXML(String id, String name, String email, String phoneNumber) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getEmail() { return email; }
        public String getPhoneNumber() { return phoneNumber; }
    }
}
