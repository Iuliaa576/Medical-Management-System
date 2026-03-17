package gui;

import domain.Appointment;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import repository.RepositoryException;
import service.AppointmentService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AppointmentsGUIController {

    private AppointmentService appointmentService;

    private final DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private ListView<Appointment> appointmentListView;

    @FXML
    private TextField appointmentIdField;

    @FXML
    private TextField patientIdField;

    @FXML
    private TextField dateTimeField;   // yyyy-MM-dd HH:mm

    @FXML
    private Button addButton;

    @FXML
    private Button updateButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button findButton;

    @FXML
    private Button resetButton;

    @FXML
    private TextField filterPatientIdField;

    @FXML
    private TextField filterDateField; // yyyy-MM-dd

    @FXML
    private Button filterPatientButton;

    @FXML
    private Button filterDateButton;

    public void setAppointmentService(AppointmentService service) {
        this.appointmentService = service;
        loadAppointments();
    }

    private void loadAppointments() {
        appointmentListView.getItems().clear();

        for (Appointment a : appointmentService.getAll().values()) {
            appointmentListView.getItems().add(a);
        }
    }

    private void addAppointment() {
        try {
            LocalDateTime dateTime =
                    LocalDateTime.parse(dateTimeField.getText(), formatter);

            appointmentService.addAppointment(
                    appointmentIdField.getText(),
                    patientIdField.getText(),
                    dateTime
            );
            clearFields();
            loadAppointments();
        } catch (RepositoryException e) {
            showError(e.getCustomMessage());
        }
    }

    private void updateAppointment() {
        try {
            LocalDateTime dateTime =
                    LocalDateTime.parse(dateTimeField.getText(), formatter);

            appointmentService.updateAppointmentByID(
                    appointmentIdField.getText(),
                    patientIdField.getText(),
                    dateTime
            );
            clearFields();
            loadAppointments();
        } catch (Exception e) {
            showError("Invalid DateTime format!\nUse: yyyy-MM-dd HH:mm");
        }
    }

    private void deleteAppointment() {
        try {
            appointmentService.deleteAppointmentByID(appointmentIdField.getText());
            clearFields();
            loadAppointments();
        } catch (RepositoryException e) {
            showError(e.getCustomMessage());
        }
    }

    private void findAppointmentByID() throws RepositoryException {
        String id = appointmentIdField.getText();

        if (id.isEmpty()) {
            System.out.println("ID is empty");
            return;
        }

        Appointment a = appointmentService.findAppointmentByID(id);

        appointmentListView.getItems().clear();
        if (a != null) {
            appointmentListView.getItems().add(a);
        } else {
            System.out.println("Appointment not found");
        }
    }

    private void filterByPatientId() {
        String patientId = filterPatientIdField.getText().toLowerCase();

        appointmentListView.getItems().clear();
        for (Appointment a : appointmentService.getAll().values()) {
            if (a.getPatientId().toLowerCase().contains(patientId)) {
                appointmentListView.getItems().add(a);
            }
        }
    }

    private void filterByDate() {
        String date = filterDateField.getText(); // yyyy-MM-dd

        appointmentListView.getItems().clear();
        for (Appointment a : appointmentService.getAll().values()) {
            if (a.getDateTime().toLocalDate().toString().equals(date)) {
                appointmentListView.getItems().add(a);
            }
        }
    }

    private void clearFields() {
        appointmentIdField.clear();
        patientIdField.clear();
        dateTimeField.clear();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    @FXML
    private void handleAdd() { addAppointment(); }

    @FXML
    private void handleUpdate() { updateAppointment(); }

    @FXML
    private void handleDelete() { deleteAppointment(); }

    @FXML
    private void handleFindById() throws RepositoryException {
        findAppointmentByID();
    }

    @FXML
    private void handleFilterByPatient() {
        filterByPatientId();
    }

    @FXML
    private void handleFilterByDate() {
        filterByDate();
    }

    @FXML
    private void handleReset() {
        appointmentIdField.clear();
        patientIdField.clear();
        dateTimeField.clear();
        filterPatientIdField.clear();
        filterDateField.clear();
        loadAppointments();
    }

    @FXML
    private void handleUndo() {
        try {
            appointmentService.undo();
            loadAppointments();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleRedo() {
        try {
            appointmentService.redo();
            loadAppointments();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }
}
