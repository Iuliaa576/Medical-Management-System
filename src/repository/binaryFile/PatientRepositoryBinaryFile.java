package repository.binaryFile;

import domain.Patient;
import repository.FileRepository;
import repository.RepositoryException;

import java.io.*;
import java.util.HashMap;

public class PatientRepositoryBinaryFile extends FileRepository<String, Patient> {

    public PatientRepositoryBinaryFile(String FileName) throws RepositoryException {
        super(FileName);
    }

    @Override
    protected void readFromFile() {
        File file = new File(FileName);
        if (!file.exists() || file.length() == 0) {
            // If file doesn't exist or is empty, initialize with an empty map
            this.elements = new HashMap<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FileName))) {
            Object obj = ois.readObject();
            if (obj instanceof HashMap<?, ?> map) {
                this.elements = (HashMap<String, Patient>) map;
            } else {
                throw new RuntimeException("Invalid file format: expected HashMap<String, Patient>");
            }
        } catch (EOFException e) {
            // The file is empty - new HashMap
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