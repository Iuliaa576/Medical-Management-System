package ui;

import service.ReportsService;

import java.util.Scanner;

public class ReportsUI {

    private final ReportsService reportsService;
    private final Scanner scanner = new Scanner(System.in);

    public ReportsUI(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    public void start() {
        while (true) {
            System.out.println("\n=== Reports Menu ===");
            System.out.println("1. Patients ordered alphabetically with appointment count");
            System.out.println("2. Next busiest day");
            System.out.println("3. Appointments grouped by weekday");
            System.out.println("4. Overlapping appointments");
            System.out.println("5. Patients grouped by email domain");
            System.out.println("0. Exit");
            System.out.print("Choose: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    report1();
                    break;
                case "2":
                    report2();
                    break;
                case "3":
                    report3();
                    break;
                case "4":
                    report4();
                    break;
                case "5":
                    report5();
                    break;
                case "0":
                    return;
                default:
                    System.out.println("Invalid option");
            }
        }
    }

    private void report1() {
        System.out.println("\n--- Patients (A->Z) with Appointment Count ---");
        reportsService.reportPatientsAlphabeticallyWithAppointmentCount()
                .forEach(System.out::println);
    }

    private void report2() {
        System.out.println("\n--- Next busiest Day ---");
        System.out.println(reportsService.reportBusiestDay());
    }

    private void report3() {
        System.out.println("\n--- Appointments Grouped by Weekday ---");

        reportsService.reportAppointmentsGroupedByWeekday()
                .forEach((day, list) -> {
                    System.out.println(day + ":");
                    list.forEach(a -> System.out.println("   " + a));
                    System.out.println();
                });
    }

    private void report4() {
        System.out.println("\n--- Overlapping Appointments ---");

        var overlaps = reportsService.reportOverlappingAppointments();

        if (overlaps.isEmpty()) {
            System.out.println("No overlapping appointments found.");
            return;
        }

        overlaps.forEach((dateTime, list) -> {
            System.out.println(dateTime + ":");
            list.forEach(a -> System.out.println("   " + a));
            System.out.println();
        });
    }

    private void report5() {
        System.out.println("\n--- Patients Grouped by Email Domain ---");

        var result = reportsService.reportPatientsGroupedByEmailDomain();

        result.forEach((domain, list) -> {
            System.out.println(domain + ":");
            list.forEach(p -> System.out.println("   " + p));
            System.out.println();
        });
    }
}
