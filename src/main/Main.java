package main;

import domain.Appointment;
import domain.Patient;
import gui.MainMenuController;
import gui.PatientsGUIController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import repository.*;
import repository.DB.AppointmentRepositoryDB;
import repository.DB.PatientRepositoryDB;
import repository.binaryFile.AppointmentRepositoryBinaryFile;
import repository.binaryFile.PatientRepositoryBinaryFile;
import repository.jsonFile.AppointmentRepositoryJSONFile;
import repository.jsonFile.PatientRepositoryJSONFile;
import repository.textFile.AppointmentRepositoryTextFile;
import repository.textFile.PatientRepositoryTextFile;
import repository.xmlFile.AppointmentRepositoryXMLFile;
import repository.xmlFile.PatientRepositoryXMLFile;
import service.AppointmentService;
import service.PatientService;
import service.ReportsService;
import undo.UndoRedoManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Main extends Application {

    public static IRepository<String, Patient> readPropetriesInitPatientRepository() {
        IRepository<String, Patient> patientRepo = null;
        try {
            InputStream is = new FileInputStream("src/settings.properties");
            Properties pr = new Properties();
            pr.load(is);

            String repoType = pr.getProperty("RepositoryType");

            if (repoType.equals("csvfile")) {
                try {
                    String repoPath = pr.getProperty("PatientsPath");
                    patientRepo = new PatientRepositoryTextFile(repoPath);
                } catch (RepositoryException e) {
                    System.out.println(e.getCustomMessage());
                }
            }

            if (repoType.equals("binaryfile")) {
                try {
                    String repoPath = pr.getProperty("PatientsPath");
                    patientRepo = new PatientRepositoryBinaryFile(repoPath);
                } catch (RepositoryException e) {
                    System.out.println(e.getCustomMessage());
                }
            }

            if (repoType.equals("jsonfile")) {
                try {
                    String repoPath = pr.getProperty("PatientsPath");
                    patientRepo = new PatientRepositoryJSONFile(repoPath);
                } catch (RepositoryException e) {
                    System.out.println(e.getCustomMessage());
                }
            }

            if  (repoType.equals("xmlfile")) {
                try {
                    String repoPath = pr.getProperty("PatientsPath");
                    patientRepo = new PatientRepositoryXMLFile(repoPath);
                }  catch (RepositoryException e) {
                    System.out.println(e.getCustomMessage());
                }
            }

            if (repoType.equals("database")){
                String URL = pr.getProperty("PatientsPath");
                patientRepo = new PatientRepositoryDB(URL);
            }

            return patientRepo;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static IRepository<String, Appointment> readPropetriesInitAppointmentRepository() {
        IRepository<String, Appointment> appointmentRepo = null;
        try {
            InputStream is = new FileInputStream("src/settings.properties");
            Properties pr = new Properties();
            pr.load(is);

            String repoType = pr.getProperty("RepositoryType");

            if (repoType.equals("csvfile")) {
                try {
                    String repoPath = pr.getProperty("AppointmentsPath");
                    appointmentRepo = new AppointmentRepositoryTextFile(repoPath);
                } catch (RepositoryException e) {
                    System.out.println(e.getCustomMessage());
                }
            }

            if (repoType.equals("binaryfile")) {
                try {
                    String repoPath = pr.getProperty("AppointmentsPath");
                    appointmentRepo = new AppointmentRepositoryBinaryFile(repoPath);
                } catch (RepositoryException e) {
                    System.out.println(e.getCustomMessage());
                }
            }

            if (repoType.equals("jsonfile")) {
                try {
                    String repoPath = pr.getProperty("AppointmentsPath");
                    appointmentRepo = new AppointmentRepositoryJSONFile(repoPath);
                } catch (RepositoryException e) {
                    System.out.println(e.getCustomMessage());
                }
            }

            if  (repoType.equals("xmlfile")) {
                try {
                    String repoPath = pr.getProperty("AppointmentsPath");
                    appointmentRepo = new AppointmentRepositoryXMLFile(repoPath);
                }  catch (RepositoryException e) {
                    System.out.println(e.getCustomMessage());
                }
            }

            if (repoType.equals("database")){
                String URL = pr.getProperty("AppointmentsPath");
                appointmentRepo = new AppointmentRepositoryDB(URL);
            }

            return appointmentRepo;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        IRepository<String, Patient> patientRepo = readPropetriesInitPatientRepository();
        IRepository<String, Appointment> appointmentRepo = readPropetriesInitAppointmentRepository();

        // ONE shared undo/redo manager
        UndoRedoManager undoRedoManager = new UndoRedoManager();

        // Initialize services
        PatientService patientService =
                new PatientService(patientRepo, null, undoRedoManager);

        AppointmentService appointmentService =
                new AppointmentService(appointmentRepo, patientService, undoRedoManager);

        patientService.setAppointmentService(appointmentService);

        ReportsService reportsService =
                new ReportsService(patientService, appointmentService);

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/gui/MainMenu.fxml"));

        Scene scene = new Scene(loader.load());

        MainMenuController controller = loader.getController();
        controller.setPatientService(patientService);
        controller.setAppointmentService(appointmentService);
        controller.setReportsService(reportsService);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);

        // Initialize repositories
//        PatientRepository patientRepo = new PatientRepository();
//        AppointmentRepository appointmentRepo = new AppointmentRepository();
//
//        patientRepo.add("P01", new Patient("P01", "Alice", "alice@yahoo.com", "0719876345"));
//        patientRepo.add("P02", new Patient("P02", "Bob", "bob@yahoo.com", "0703987123"));
//        patientRepo.add("P03", new Patient("P03", "John", "john@yahoo.com", "0729674033"));
//        patientRepo.add("P04", new Patient("P04", "Carl", "carl@yahoo.com", "0719876345"));
//        patientRepo.add("P05", new Patient("P05", "James", "james@yahoo.com", "0719876345"));
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        appointmentRepo.add("A01", new Appointment("A01", "P01", LocalDateTime.parse("2025-10-30 14:00", formatter)));
//        appointmentRepo.add("A02", new Appointment("A02", "P02", LocalDateTime.parse("2025-10-31 16:00", formatter)));
//        appointmentRepo.add("A03", new Appointment("A03", "P03", LocalDateTime.parse("2025-11-02 18:00", formatter)));
//        appointmentRepo.add("A04", new Appointment("A04", "P04", LocalDateTime.parse("2025-12-05 12:00", formatter)));
//        appointmentRepo.add("A05", new Appointment("A05", "P05", LocalDateTime.parse("2025-12-05 12:00", formatter)));

//        IRepository<String, Patient> patientRepo = readPropetriesInitPatientRepository();
//        IRepository<String, Appointment> appointmentRepo = readPropetriesInitAppointmentRepository();
//
//        // Initialize services
//        PatientService patientService = new PatientService(patientRepo, null);
//        AppointmentService appointmentService = new AppointmentService(appointmentRepo, patientService);
//        patientService.setAppointmentService(appointmentService);
//        ReportsService reportsService = new ReportsService(patientService, appointmentService);
//
//        // Initialize UIs
//        PatientUI patientUI = new PatientUI(patientService);
//        AppointmentUI appointmentUI = new AppointmentUI(appointmentService, patientService);
//        ReportsUI reportsUI = new ReportsUI(reportsService);

        // main.Main menu
//        Scanner scanner = new Scanner(System.in);
//        while (true) {
//            System.out.println("\n=== Dentist Management System ===");
//            System.out.println("1. Manage Patients");
//            System.out.println("2. Manage Appointments");
//            System.out.println("3. Reports");
//            System.out.println("0. Exit");
//            System.out.print("Choose an option: ");
//
//            String choice = scanner.nextLine();
//            switch (choice) {
//                case "1":
//                    patientUI.start();
//                    break;
//                case "2":
//                    appointmentUI.start();
//                    break;
//                case "3":
//                    reportsUI.start();
//                    break;
//                case "0":
//                    System.out.println("Exiting...");
//                    return;
//                default:
//                    System.out.println("Invalid option");
//            }
//        }
    }
}