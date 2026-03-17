package util;

import domain.Patient;

import java.util.List;

public final class BulkPatientUpdaterThreads {
    private BulkPatientUpdaterThreads() {}

    public static void updateRiskStatus(List<Patient> patients, int threadCount) throws InterruptedException {
        if (threadCount <= 0) throw new IllegalArgumentException("threadCount must be > 0");

        int n = patients.size();
        int chunk = (n + threadCount - 1) / threadCount;

        Thread[] threads = new Thread[threadCount];

        for (int t = 0; t < threadCount; t++) {
            final int start = t * chunk;
            final int end = Math.min(start + chunk, n);

            threads[t] = new Thread(() -> {
                for (int i = start; i < end; i++) {
                    Patient p = patients.get(i);
//                    if (p.getAge() > 60) {
//                        p.setHealthRiskStatus("high risk");
//                    }
                }
            }, "RiskUpdater-" + t);

            threads[t].start();
        }

        for (Thread th : threads) {
            th.join();
        }
    }
}
