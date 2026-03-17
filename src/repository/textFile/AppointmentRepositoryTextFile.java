package repository.textFile;

import domain.Appointment;
import repository.FileRepository;
import repository.RepositoryException;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppointmentRepositoryTextFile extends FileRepository<String, Appointment> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd/HH:mm");

    public AppointmentRepositoryTextFile(String fileName) throws RepositoryException {
        super(fileName);
    }

    @Override
    protected void readFromFile() throws RepositoryException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length == 3) {
                    String id = tokens[0];
                    String patientId = tokens[1];
                    String dateTime = tokens[2];

                    LocalDateTime enteredDateTime = LocalDateTime.parse(dateTime, FORMATTER);
                    Appointment appointment = new Appointment(id, patientId, enteredDateTime);
                    super.add(id, appointment);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected void writeToFile() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(FileName))) {
            for (Appointment appointment : elements.values()) {
                bufferedWriter.write(
                        appointment.getId() + "," +
                                appointment.getPatientId() + "," +
                                appointment.getDateTime().format(FORMATTER)
                );
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
