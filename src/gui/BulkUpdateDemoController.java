package gui;

import domain.Patient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import util.BulkPatientUpdaterExecutor;
import util.BulkPatientUpdaterThreads;
import util.PatientGenerator;

import java.util.List;

public class BulkUpdateDemoController {

    @FXML private TextField countField;
    @FXML private TextField threadsField;
    @FXML private TextArea outputArea;
    @FXML private ListView<Patient> patientsListView;

    private List<Patient> patients;

    private final ObservableList<Patient> patientsObservable = FXCollections.observableArrayList();

    public void init() {
        setupListView();
        log("Ready.");
    }

    private void setupListView() {
        patientsListView.setItems(patientsObservable);

        // Custom rendering for each row
        patientsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Patient p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null);
                } else {
                    String risk = p.getHealthRiskStatus();
                    if (risk == null) risk = "low risk";
                    setText(p.getId() + " | " + p.getName() +
                            " | " + p.getEmail() +
                            " | " + p.getPhoneNumber() +
                            " | age=" + p.getAge() +
                            " | " + risk);
                }
            }
        });
    }

    private int readInt(TextField field, int def) {
        try {
            int v = Integer.parseInt(field.getText().trim());
            return v > 0 ? v : def;
        } catch (Exception e) {
            return def;
        }
    }

    private void log(String msg) {
        Platform.runLater(() -> outputArea.appendText(msg + "\n"));
    }

    // helper: refresh list so risk changes show after updates
    private void refreshListView() {
        Platform.runLater(() -> patientsListView.refresh());
    }

    @FXML
    private void handleGenerate() {
        int count = readInt(countField, 100_000);

        new Thread(() -> {
            log("Generating " + count + " patients...");
            patients = PatientGenerator.generate(count);

            // Put into ListView on FX thread
            Platform.runLater(() -> {
                patientsObservable.setAll(patients);
                if (!patientsObservable.isEmpty()) {
                    patientsListView.scrollTo(0);
                    patientsListView.getSelectionModel().select(0);
                }
            });

            log("Generation finished.");
        }).start();
    }

    @FXML
    private void handleThreads() {
        if (patients == null) {
            log("Generate patients first!");
            return;
        }

        int threads = readInt(threadsField, 8);

        new Thread(() -> {
            try {
                log("Running update using Threads (" + threads + ")...");
                long t1 = System.nanoTime();
                BulkPatientUpdaterThreads.updateRiskStatus(patients, threads);
                long t2 = System.nanoTime();

                long highRisk = patients.stream()
                        .filter(p -> "high risk".equals(p.getHealthRiskStatus()))
                        .count();

                log("Done in " + ((t2 - t1) / 1_000_000) + " ms");
                log("High risk patients: " + highRisk);

                // show updated statuses
                refreshListView();

            } catch (Exception e) {
                log("Error: " + e.getMessage());
            }
        }).start();
    }

    @FXML
    private void handleExecutor() {
        if (patients == null) {
            log("Generate patients first!");
            return;
        }

        int threads = readInt(threadsField, 8);

        new Thread(() -> {
            try {
                log("Running update using ExecutorService (" + threads + ")...");
                long t1 = System.nanoTime();
                BulkPatientUpdaterExecutor.updateRiskStatus(patients, threads);
                long t2 = System.nanoTime();

                long highRisk = patients.stream()
                        .filter(p -> "high risk".equals(p.getHealthRiskStatus()))
                        .count();

                log("Done in " + ((t2 - t1) / 1_000_000) + " ms");
                log("High risk patients: " + highRisk);

                // show updated statuses
                refreshListView();

            } catch (Exception e) {
                log("Error: " + e.getMessage());
            }
        }).start();
    }
}
