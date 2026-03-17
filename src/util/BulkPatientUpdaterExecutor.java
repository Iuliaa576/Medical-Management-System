package util;

import domain.Patient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public final class BulkPatientUpdaterExecutor {
    private BulkPatientUpdaterExecutor() {}

    public static void updateRiskStatus(List<Patient> patients, int threadCount) throws InterruptedException {
        if (threadCount <= 0) throw new IllegalArgumentException("threadCount must be > 0");

        ExecutorService pool = Executors.newFixedThreadPool(threadCount);

        int n = patients.size();
        int chunk = (n + threadCount - 1) / threadCount;

        List<Callable<Void>> tasks = new ArrayList<>(threadCount);

        for (int t = 0; t < threadCount; t++) {
            final int start = t * chunk;
            final int end = Math.min(start + chunk, n);

            tasks.add(() -> {
                for (int i = start; i < end; i++) {
                    Patient p = patients.get(i);
//                    if (p.getAge() > 60) {
////                        p.setHealthRiskStatus("high risk");
//                    }
                }
                return null;
            });
        }

        try {
            pool.invokeAll(tasks); // waits for all tasks
        } finally {
            pool.shutdown();
            pool.awaitTermination(1, TimeUnit.MINUTES);
        }
    }
}
