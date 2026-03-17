package repository.binaryFile;

import domain.Appointment;
import repository.FileRepository;
import repository.RepositoryException;

import java.io.*;
import java.util.HashMap;

public class AppointmentRepositoryBinaryFile extends FileRepository<String, Appointment> {

    public AppointmentRepositoryBinaryFile(String FileName) throws RepositoryException {
        super(FileName);
    }

    @Override
    protected void readFromFile() {
        File file = new File(FileName);
        if (!file.exists() || file.length() == 0) {
            // No file yet, start with an empty repository
            this.elements = new HashMap<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileName))) {
            Object obj = ois.readObject();
            if (obj instanceof HashMap<?, ?> map) {
                this.elements = (HashMap<String, Appointment>) map;
            } else {
                throw new RuntimeException("Invalid file format: expected HashMap<String, Appointment>");
            }
        } catch (EOFException e) {
            // Empty file — ignore
            this.elements = new HashMap<>();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error reading from binary file: " + e.getMessage());
        }
    }

    @Override
    protected void writeToFile() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(this.FileName));
            oos.writeObject(this.elements);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}