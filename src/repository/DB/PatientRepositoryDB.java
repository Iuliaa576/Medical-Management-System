package repository.DB;

import domain.Patient;
import repository.IRepository;
import repository.RepositoryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PatientRepositoryDB implements IRepository<String, Patient> {
    private String URL;

    public PatientRepositoryDB(String URL) {
        this.URL = URL;
    }

    @Override
    public void add(String id, Patient patient) throws RepositoryException {
        String sql = "INSERT INTO patient (id, name, email, phoneNumber, age) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, patient.getId());
            st.setString(2, patient.getName());
            st.setString(3, patient.getEmail());
            st.setString(4, patient.getPhoneNumber());
            st.setInt(5, patient.getAge());
            st.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error adding patient: " + e.getMessage());
        }
    }

    @Override
    public Optional<Patient> delete(String id) throws RepositoryException {
        Optional<Patient> existing = findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }

        String sql = "DELETE FROM patient WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, id);
            st.executeUpdate();

            return existing;

        } catch (SQLException e) {
            throw new RepositoryException("Error deleting patient: " + e.getMessage());
        }
    }

    @Override
    public void modify(String id, Patient patient) throws RepositoryException {
        String sql = "UPDATE patient SET name = ?, email = ?, phoneNumber = ?, age = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, patient.getName());
            st.setString(2, patient.getEmail());
            st.setString(3, patient.getPhoneNumber());
            st.setInt(4, patient.getAge());

            // This ensures we modify the correct record
            st.setString(5, id);

            int rowsUpdated = st.executeUpdate();
            if (rowsUpdated == 0) {
                System.out.println("No patient found with ID: " + id);
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error modifying patient: " + e.getMessage());
        }
    }

    @Override
    public Optional<Patient> findById(String id) throws RepositoryException {
        String sql = "SELECT * FROM patient WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phoneNumber = rs.getString("phoneNumber");
                int age = rs.getInt("age");

                return Optional.of(new Patient(id, name, email, phoneNumber, age));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RepositoryException("Error finding patient: " + e.getMessage());
        }
    }

    @Override
    public Iterable<Patient> getAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT * FROM patient";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String phoneNumber = rs.getString("phoneNumber");
                int age = rs.getInt("age");

                patients.add(new Patient(id, name, email, phoneNumber, age));
            }

        } catch (SQLException e) {
            System.out.printf("Error getting all patients: %s\n", e.getMessage());
        }

        return patients;
    }
}
