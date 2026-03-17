package repository.textFile;

import domain.Patient;
import repository.FileRepository;
import repository.RepositoryException;

import java.io.*;

public class PatientRepositoryTextFile extends FileRepository<String, Patient> {


    public PatientRepositoryTextFile(String FileName) throws RepositoryException {
        super(FileName);
    }

    @Override
    protected void readFromFile() throws RepositoryException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FileName))) {
            String line = bufferedReader.readLine();
            while (line != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 4) {
                    String id = (tokens[0]);
                    String name = tokens[1];
                    String email = tokens[2];
                    String phoneNumber = tokens[3];
                    int age = 30;

                    Patient patient = new Patient(id, name, email, phoneNumber, age);
                    super.add(id, patient);
                }
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void writeToFile() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FileName))) {
            for (Patient patient : elements.values()) {
                bufferedWriter.write(patient.getId() + "," +
                        patient.getName() + "," +
                        patient.getEmail() + "," +
                        patient.getPhoneNumber());
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
