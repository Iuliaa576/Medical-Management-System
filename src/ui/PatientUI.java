//package ui;
//
//import domain.Patient;
//import filter.AbstractFilter;
//import filter.FilterPatientByEmail;
//import filter.FilterPatientByName;
//import repository.RepositoryException;
//import service.PatientService;
//
//import java.util.HashMap;
//import java.util.Scanner;
//
//public class PatientUI {
//
//    private final PatientService service;
//    private final Scanner scanner = new Scanner(System.in);
//
//    public PatientUI(PatientService service) {
//        this.service = service;
//    }
//
//    public void start() {
//        while (true) {
//            System.out.println("\n=== Patient Management ===");
//            System.out.println("1. Add Patient");
//            System.out.println("2. Update Patient");
//            System.out.println("3. Delete Patient");
//            System.out.println("4. Find Patient by ID");
//            System.out.println("5. View All Patients");
//            System.out.println("6. Filter Patients by Name");
//            System.out.println("7. Filter Patients by Email");
//            System.out.println("0. Exit");
//            System.out.println("Choose an option: ");
//
//            String choice = scanner.nextLine();
//            try {
//                switch (choice) {
//                    case "1":
//                        addPatient();
//                        break;
//                    case "2":
//                        updatePatient();
//                        break;
//                    case "3":
//                        deletePatient();
//                        break;
//                    case "4":
//                        findById();
//                        break;
//                    case "5":
//                        viewAll();
//                        break;
//                    case "6":
//                        filterPatientsByName();
//                        break;
//                    case "7":
//                        filterPatientsByEmail();
//                        break;
//                    case "0":
//                        return;
//                    default:
//                        System.out.println("Invalid option");
//                }
//            } catch (RuntimeException e) {
//                System.out.println("Error: " + e.getMessage());
//            }
//        }
//    }
//
//    private void addPatient() {
//        try {
//            System.out.println("Enter Patient ID: ");
//            String id = scanner.nextLine();
//            System.out.println("Enter Patient Name: ");
//            String name = scanner.nextLine();
//            System.out.println("Enter Patient Email: ");
//            String email = scanner.nextLine();
//            System.out.println("Enter Patient Phone Number: ");
//            String phoneNumber = scanner.nextLine();
//
//            service.addPatient(id, name, email, phoneNumber, age);
//            System.out.println("Patient has been added successfully");
//        } catch (RepositoryException e) {
//            System.out.println(e.getCustomMessage());
//        }
//    }
//
//    private void updatePatient() {
//        try {
//            System.out.println("Enter Patient ID: ");
//            String id = scanner.nextLine();
//            System.out.println("Enter Patient Name: ");
//            String name = scanner.nextLine();
//            System.out.println("Enter Patient Email: ");
//            String email = scanner.nextLine();
//            System.out.println("Enter Patient Phone Number: ");
//            String phoneNumber = scanner.nextLine();
//
//            service.updatePatientByID(id, name, email, phoneNumber);
//            System.out.println("Patient has been updated successfully");
//        } catch (RepositoryException e) {
//            System.out.println(e.getCustomMessage());
//        }
//    }
//
//    private void deletePatient() {
//        try {
//            System.out.println("Enter Patient ID: ");
//            String id = scanner.nextLine();
//            service.deletePatientByID(id);
//            System.out.println("Patient has been deleted successfully");
//        } catch (RepositoryException e) {
//            System.out.println(e.getCustomMessage());
//        }
//    }
//
//    private void findById() {
//        try {
//            System.out.println("Enter Patient ID: ");
//            String id = scanner.nextLine();
//            Patient patient = service.findPatientByID(id);
//            if (patient != null) {
//                System.out.println(patient);
//            } else {
//                System.out.println("Patient not found");
//            }
//        } catch (RepositoryException e) {
//            System.out.println(e.getCustomMessage());
//        }
//    }
//
//    private void viewAll() {
//        service.getAll().values().forEach(System.out::println);
//    }
//
//    private void filterPatientsByName() {
//        try {
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Name: ");
//            String name = scanner.nextLine();
//            AbstractFilter<Patient> filter = p -> p.getName().toLowerCase().contains(name.toLowerCase());
//            HashMap<String, Patient> patients = service.filterPatients(filter);
//            patients.values().forEach(System.out::println);
//        } catch (RepositoryException e) {
//            System.out.println(e.getCustomMessage());
//        }
//    }
//
//    private void filterPatientsByEmail() {
//        try {
//            Scanner scanner = new Scanner(System.in);
//            System.out.println("Email: ");
//            String email = scanner.nextLine();
//            AbstractFilter<Patient> filter = p -> p.getEmail().equalsIgnoreCase(email);
//            HashMap<String, Patient> patients = service.filterPatients(filter);
//            patients.values().forEach(System.out::println);
//        } catch (RepositoryException e) {
//            System.out.println(e.getCustomMessage());
//        }
//    }
//}
