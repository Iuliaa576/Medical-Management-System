package service;

import domain.Appointment;
import domain.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ReportsService {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    public ReportsService(PatientService patientService, AppointmentService appointmentService) {
        this.patientService = patientService;
        this.appointmentService = appointmentService;
    }

    // Report 1 - Patients sorted alphabetically with appointment count
    public List<String> reportPatientsAlphabeticallyWithAppointmentCount() {

        // Convert Iterable to stream
        List<Patient> patients = new ArrayList<>();
        patientService.getAll().values().forEach(patients::add);

        List<Appointment> appointments = new ArrayList<>();
        appointmentService.getAll().values().forEach(appointments::add);

        // Stream solution
        return patients.stream()
                .sorted(Comparator.comparing(Patient::getName)) // alphabetical
                .map(p -> {
                    long count = appointments.stream()
                            .filter(a -> a.getPatientId().equals(p.getId()))
                            .count();
                    return p.getName() + " (" + p.getId() + ") -> " + count + " appointments";
                })
                .collect(Collectors.toList());
    }

    // Report 2 — Next busiest day (date with most appointments)
    public String reportBusiestDay() {

        // Extract all appointments
        List<Appointment> appointments = new ArrayList<>();
        appointmentService.getAll().values().forEach(appointments::add);

        if (appointments.isEmpty()) {
            return "No appointments available.";
        }

        // Group by date (LocalDate) and count
        Map<LocalDate, Long> counts = appointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDateTime().toLocalDate(),
                        Collectors.counting()
                ));

        // Find date with maximum count
        Map.Entry<LocalDate, Long> busiest = counts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get();

        return "Next busiest day: " + busiest.getKey() +
                " -> " + busiest.getValue() + " appointment(s)";
    }

    // Report 3 — Appointments grouped by weekday
    public Map<String, List<String>> reportAppointmentsGroupedByWeekday() {

        List<Appointment> appointments = new ArrayList<>();
        appointmentService.getAll().values().forEach(appointments::add);

        // Group by weekday name: MONDAY, TUESDAY, ...
        Map<String, List<String>> result = appointments.stream()
                .collect(Collectors.groupingBy(
                        a -> a.getDateTime().getDayOfWeek().toString(), // e.g., "MONDAY"
                        Collectors.mapping(
                                a -> a.getId() + " -> " + a.getDateTime() +
                                        " (Patient " + a.getPatientId() + ")",
                                Collectors.toList()
                        )
                ));

        // Sort weekdays (Monday first)
        Map<String, List<String>> sorted = new LinkedHashMap<>();
        List<String> days = Arrays.asList(
                "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
                "FRIDAY", "SATURDAY", "SUNDAY"
        );

        days.forEach(day -> {
            if (result.containsKey(day)) {
                sorted.put(day, result.get(day));
            }
        });

        return sorted;
    }

    // Report 4 — Overlapping appointments (same date/time)
    public Map<String, List<String>> reportOverlappingAppointments() {

        List<Appointment> appointments = new ArrayList<>();
        appointmentService.getAll().values().forEach(appointments::add);

        // Group by exact LocalDateTime
        Map<LocalDateTime, List<Appointment>> grouped =
                appointments.stream()
                        .collect(Collectors.groupingBy(Appointment::getDateTime));

        // Filter groups with 2+ appointments (overlaps)
        Map<String, List<String>> overlaps = new LinkedHashMap<>();

        grouped.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)  // only overlaps
                .sorted(Map.Entry.comparingByKey())     // sort by datetime
                .forEach(e -> {
                    String dateTime = e.getKey().toString();
                    List<String> lines = e.getValue().stream()
                            .map(a -> a.getId() + " -> Patient " + a.getPatientId())
                            .collect(Collectors.toList());
                    overlaps.put(dateTime, lines);
                });

        return overlaps;
    }

    // Report 5 — Patients grouped by email domain
    public Map<String, List<String>> reportPatientsGroupedByEmailDomain() {

        List<Patient> patients = new ArrayList<>();
        patientService.getAll().values().forEach(patients::add);

        // Group by domain name (after the @ sign)
        Map<String, List<String>> grouped = patients.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getEmail().substring(p.getEmail().indexOf("@") + 1),
                        Collectors.mapping(
                                p -> p.getId() + " — " + p.getName(),
                                Collectors.toList()
                        )
                ));

        // Sort results alphabetically by domain
        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> a, // merge function (unused)
                        LinkedHashMap::new
                ));
    }
}
