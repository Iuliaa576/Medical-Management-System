package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import service.ReportsService;

import java.util.List;
import java.util.Map;

public class ReportsGUIController {

    private ReportsService reportsService;

    @FXML
    private ListView<String> reportListView;

    public void setReportsService(ReportsService service) {
        this.reportsService = service;
    }

    // Patients sorted alphabetically with appointment count
    @FXML
    private void report1() {
        reportListView.getItems().clear();

        List<String> result =
                reportsService.reportPatientsAlphabeticallyWithAppointmentCount();

        reportListView.getItems().addAll(result);
    }

    // Next busiest day
    @FXML
    private void report2() {
        reportListView.getItems().clear();

        String result = reportsService.reportBusiestDay();

        reportListView.getItems().add(result);
    }

    // Appointments grouped by weekday
    @FXML
    private void report3() {
        reportListView.getItems().clear();

        Map<String, List<String>> result =
                reportsService.reportAppointmentsGroupedByWeekday();

        result.forEach((day, list) -> {
            reportListView.getItems().add("=== " + day + " ===");
            reportListView.getItems().addAll(list);
            reportListView.getItems().add(""); // empty line
        });
    }

    // Overlapping appointments
    @FXML
    private void report4() {
        reportListView.getItems().clear();

        Map<String, List<String>> result =
                reportsService.reportOverlappingAppointments();

        if (result.isEmpty()) {
            reportListView.getItems().add("No overlapping appointments found.");
            return;
        }

        result.forEach((dateTime, list) -> {
            reportListView.getItems().add("=== " + dateTime + " ===");
            reportListView.getItems().addAll(list);
            reportListView.getItems().add("");
        });
    }

    // Patients grouped by email domain
    @FXML
    private void report5() {
        reportListView.getItems().clear();

        Map<String, List<String>> result =
                reportsService.reportPatientsGroupedByEmailDomain();

        result.forEach((domain, list) -> {
            reportListView.getItems().add("=== " + domain + " ===");
            reportListView.getItems().addAll(list);
            reportListView.getItems().add("");
        });
    }

    @FXML
    private void handleReport1() {
        report1();
    }

    @FXML
    private void handleReport2() {
        report2();
    }

    @FXML
    private void handleReport3() {
        report3();
    }

    @FXML
    private void handleReport4() {
        report4();
    }

    @FXML
    private void handleReport5() {
        report5();
    }

    @FXML
    private void handleReset() {
        reportListView.getItems().clear();
    }
}
