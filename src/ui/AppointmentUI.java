package ui;

import domain.Appointment;
import filter.AbstractFilter;
import filter.FilterAppointmentByDateTime;
import filter.FilterAppointmentByPatientId;
import repository.RepositoryException;
import service.AppointmentService;
import service.PatientService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Scanner;

public class AppointmentUI {


    private final AppointmentService service;
    private final PatientService patientService;
    private final Scanner scanner = new Scanner(System.in);

    public AppointmentUI(AppointmentService service, PatientService patientService) {
        this.service = service;
        this.patientService = patientService;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Appointment Management ===");
            System.out.println("1. Add Appointment");
            System.out.println("2. Update Appointment");
            System.out.println("3. Delete Appointment");
            System.out.println("4. Find Appointment by ID");
            System.out.println("5. View All Appointments");
            System.out.println("6. Filter Appointments by Patient ID");
            System.out.println("7. Filter Appointments by Date and Time");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        addAppointment();
                        break;
                    case "2":
                        updateAppointment();
                        break;
                    case "3":
                        deleteAppointment();
                        break;
                    case "4":
                        findById();
                        break;
                    case "5":
                        viewAll();
                        break;
                    case "6":
                        filterAppointmentsByPatientId();
                        break;
                    case "7":
                        filterAppointmentsByDateAndTime();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Invalid option");
                }
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void addAppointment() {
        try {
            System.out.print("Enter Appointment ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter Patient ID: ");
            String patientId = scanner.nextLine();
            System.out.print("Enter Date and Time (e.g., 2025-10-20 14:30): ");
            String dateTime = scanner.nextLine();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            try {
                LocalDateTime enteredDateTime = LocalDateTime.parse(dateTime, formatter);

                service.addAppointment(id, patientId, enteredDateTime);
                System.out.println("Appointment has been added successfully!");

            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm");
            }
        } catch (RepositoryException e) {
            System.out.println(e.getCustomMessage());
        }
    }

    private void updateAppointment() {
        try {
            System.out.print("Enter Appointment ID: ");
            String id = scanner.nextLine();
            System.out.print("Enter Patient ID: ");
            String patientId = scanner.nextLine();
            System.out.print("Enter Date and Time (e.g., 2025-10-20 14:30): ");
            String dateTime = scanner.nextLine();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            try {
                LocalDateTime enteredDateTime = LocalDateTime.parse(dateTime, formatter);

                service.updateAppointmentByID(id, patientId, enteredDateTime);
                System.out.println("Appointment has been updated successfully!");

            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd HH:mm");
            }
        } catch (RepositoryException e) {
            System.out.println(e.getCustomMessage());
        }
    }

    private void deleteAppointment() {
        try {
            System.out.print("Enter Appointment ID: ");
            String id = scanner.nextLine();
            service.deleteAppointmentByID(id);
            System.out.println("Appointment has been deleted successfully!");
        } catch (RepositoryException e) {
            System.out.println(e.getCustomMessage());
        }
    }

    private void findById() {
        try {
            System.out.print("Enter Appointment ID: ");
            String id = scanner.nextLine();
            Appointment appointment = service.findAppointmentByID(id);
            if (appointment != null) {
                System.out.println(appointment);
            } else {
                System.out.println("Appointment not found");
            }
        } catch (RepositoryException e) {
            System.out.println(e.getCustomMessage());
        }
    }

    private void viewAll() {
        service.getAll().values().forEach(System.out::println);
    }

    private void filterAppointmentsByPatientId() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Patient ID: ");
            String patientId = scanner.nextLine();
            AbstractFilter<Appointment> filter = a -> a.getPatientId().equals(patientId);
            HashMap<String, Appointment> appointments = service.filterAppointments(filter);
            appointments.values().forEach(System.out::println);
        } catch (RepositoryException e) {
            System.out.println(e.getCustomMessage());
        }
    }

    private void filterAppointmentsByDateAndTime() {
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Date and Time (e.g., 2025-10-20 14:30): ");
            String dateTime = scanner.nextLine();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime enteredDateTime = LocalDateTime.parse(dateTime, formatter);
            AbstractFilter<Appointment> filter = a -> a.getDateTime().equals(enteredDateTime);
            HashMap<String, Appointment> appointments = service.filterAppointments(filter);
            appointments.values().forEach(System.out::println);
        } catch (RepositoryException e) {
            System.out.println(e.getCustomMessage());
        }
    }
}
