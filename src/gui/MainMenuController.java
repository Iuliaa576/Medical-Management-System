package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.AppointmentService;
import service.PatientService;
import service.ReportsService;

public class MainMenuController {

    private PatientService patientService;
    private AppointmentService appointmentService;
    private ReportsService reportsService;

    public void setPatientService(PatientService patientService) {
        this.patientService = patientService;
    }

    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    public void setReportsService(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @FXML
    private void openPatients() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/gui/PatientsGUI.fxml"));

            Scene scene = new Scene(loader.load());

            PatientsGUIController controller =
                    loader.getController();

            controller.setPatientService(patientService);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Patient Management");
            stage.setWidth(1100);
            stage.setHeight(700);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openAppointments() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/gui/AppointmentsGUI.fxml"));

            Scene scene = new Scene(loader.load());

            AppointmentsGUIController controller =
                    loader.getController();

            controller.setAppointmentService(appointmentService);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Appointment Management");
            stage.setWidth(1100);
            stage.setHeight(700);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openReports() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/gui/ReportsGUI.fxml"));

            Scene scene = new Scene(loader.load());

            ReportsGUIController controller =
                    loader.getController();

            controller.setReportsService(reportsService);

            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Show Reports");
            stage.setWidth(1100);
            stage.setHeight(700);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openBulkUpdateDemo() {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource("/gui/BulkUpdateDemo.fxml"));

            Scene scene = new Scene(loader.load());

            BulkUpdateDemoController controller = loader.getController();
            controller.init(); // no services needed, demo is in-memory

            Stage stage = new Stage();
            stage.setTitle("Bulk Update Demo (Multithreading)");
            stage.setScene(scene);
            stage.setWidth(700);
            stage.setHeight(500);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
