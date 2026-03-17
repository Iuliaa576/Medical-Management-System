package gui;

import domain.Patient;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import repository.RepositoryException;
import service.PatientService;

public class PatientsGUIController {

    private PatientService patientService;

    @FXML
    private ListView<Patient> patientListView;

    @FXML
    private TextField idField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private TextField ageField;

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
    private TextField filterNameField;

    @FXML
    private TextField filterEmailField;

    @FXML
    private Button filterNameButton;

    @FXML
    private Button filterEmailButton;

    public void setPatientService(PatientService service) {
        this.patientService = service;
        loadPatients();
    }

    private int readAgeOrShowError() throws RepositoryException {
        try {
            int age = Integer.parseInt(ageField.getText());
            if (age < 0) throw new NumberFormatException();
            return age;
        } catch (NumberFormatException e) {
            throw new RepositoryException("Age must be a non-negative integer.");
        }
    }

    private void loadPatients() {
        patientListView.getItems().clear();

        for (Patient p : patientService.getAll().values()) {
            patientListView.getItems().add(p);
        }
    }

    private void addPatient() {
        try {
            int age = readAgeOrShowError();
            patientService.addPatient(
                    idField.getText(),
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    age
            );
            clearFields();
            loadPatients();
        } catch (RepositoryException e) {
            showError(e.getCustomMessage());
        }
    }

    private void updatePatient() {
        try {
            int age = readAgeOrShowError();
            patientService.updatePatientByID(
                    idField.getText(),
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    age
            );
            clearFields();
            loadPatients();
        } catch (RepositoryException e) {
            showError(e.getCustomMessage());
        }
    }

    private void deletePatient() {
        try {
            patientService.deletePatientByID(idField.getText());
            clearFields();
            loadPatients();
        } catch (RepositoryException e) {
            showError(e.getCustomMessage());
        }
    }

    private void findPatientByID() throws RepositoryException {
        String id = idField.getText();

        if (id.isEmpty()) {
            System.out.println("ID is empty");
            return;
        }

        Patient p = patientService.findPatientByID(id);

        patientListView.getItems().clear();
        if (p != null) {
            patientListView.getItems().add(p);
        } else {
            System.out.println("Patient not found");
        }
    }

    private void filterPatientByName() {
        String name = filterNameField.getText().toLowerCase();

        patientListView.getItems().clear();
        for (Patient p : patientService.getAll().values()) {
            if (p.getName().toLowerCase().contains(name)) {
                patientListView.getItems().add(p);
            }
        }
    }

    private void filterPatientByEmail() {
        String email = filterEmailField.getText().toLowerCase();

        patientListView.getItems().clear();
        for (Patient p : patientService.getAll().values()) {
            if (p.getEmail().toLowerCase().contains(email)) {
                patientListView.getItems().add(p);
            }
        }
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        ageField.clear();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.showAndWait();
    }

    @FXML
    private void handleAdd() { addPatient(); }

    @FXML
    private void handleUpdate() { updatePatient(); }

    @FXML
    private void handleDelete() { deletePatient(); }

    @FXML
    private void handleFindById() throws RepositoryException {
        findPatientByID();
    }

    @FXML
    private void handleFilterByName() {
        filterPatientByName();
    }

    @FXML
    private void handleFilterByEmail() {
        filterPatientByEmail();
    }

    @FXML
    private void handleReset() {
        idField.clear();
        nameField.clear();
        emailField.clear();
        phoneField.clear();
        filterNameField.clear();
        filterEmailField.clear();

        loadPatients();
    }

    @FXML
    private void handleUndo() {
        try {
            patientService.undo();
            loadPatients();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleRedo() {
        try {
            patientService.redo();
            loadPatients();
        } catch (Exception e) {
            showError(e.getMessage());
        }
    }
}
