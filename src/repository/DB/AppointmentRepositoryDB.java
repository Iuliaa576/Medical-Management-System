package repository.DB;

import domain.Appointment;
import repository.IRepository;
import repository.RepositoryException;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppointmentRepositoryDB implements IRepository<String, Appointment> {
    private final String URL;

    public AppointmentRepositoryDB(String URL) {
        this.URL = URL;
    }

    @Override
    public void add(String id, Appointment appointment) throws RepositoryException {
        String sql = "INSERT INTO appointment (id, patientId, dateTime) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, appointment.getId());
            st.setString(2, appointment.getPatientId());

            // Store LocalDateTime as a formatted string
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            st.setString(3, appointment.getDateTime().format(formatter));

            st.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error adding appointment: " + e.getMessage());
        }
    }

    @Override
    public Optional<Appointment> delete(String id) throws RepositoryException {
        Optional<Appointment> existing = findById(id);
        if (existing.isEmpty()) {
            return Optional.empty();
        }

        String sql = "DELETE FROM appointment WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, id);
            st.executeUpdate();
            return existing;

        } catch (SQLException e) {
            throw new RepositoryException("Error deleting appointment: " + e.getMessage());
        }
    }

    @Override
    public void modify(String id, Appointment appointment) throws RepositoryException {
        String sql = "UPDATE appointment SET patientId = ?, dateTime = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, appointment.getPatientId());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            st.setString(2, appointment.getDateTime().format(formatter));

            // This ensures we modify the correct record
            st.setString(3, id);
            st.executeUpdate();

        } catch (SQLException e) {
            throw new RepositoryException("Error modifying appointment: " + e.getMessage());
        }
    }

    @Override
    public Optional<Appointment> findById(String id) throws RepositoryException {
        String sql = "SELECT * FROM appointment WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, id);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String appointmentId = rs.getString("id");
                String patientId = rs.getString("patientId");
                String dateTimeString = rs.getString("dateTime");

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

                return Optional.of(new Appointment(appointmentId, patientId, dateTime));
            }

            return Optional.empty();

        } catch (SQLException e) {
            throw new RepositoryException("Error finding appointment: " + e.getMessage());
        }
    }

    @Override
    public Iterable<Appointment> getAll() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointment";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement st = conn.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            while (rs.next()) {
                String id = rs.getString("id");
                String patientId = rs.getString("patientId");
                String dateTimeString = rs.getString("dateTime");
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

                appointments.add(new Appointment(id, patientId, dateTime));
            }

        } catch (SQLException e) {
            System.out.printf("Error getting all appointments: %s\n", e.getMessage());
        }

        return appointments;
    }
}

