package util;

import domain.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class PatientGenerator {
    private PatientGenerator() {}

    public static List<Patient> generate(int count) {
        Random rnd = new Random(42);
        List<Patient> patients = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            String id = "P" + i;
            String name = "Patient_" + i;
            String email = "patient" + i + "@mail.com";
            String phone = "07" + (10000000 + rnd.nextInt(90000000));
            int age = 1 + rnd.nextInt(100); // 1..100

            patients.add(new Patient(id, name, email, phone, age));
        }
        return patients;
    }
}
