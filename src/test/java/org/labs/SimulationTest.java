package org.labs;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class SimulationTest {
    private static final boolean MULTITHREADED_TESTING = true;
    private static final int SIMULATION_COUNT = 15;
    private static final int ACCEPTABLE_FAIRNESS_DELTA_PERCENT = 30;
    private static final int SIMULATION_TIMEOUT_MILLIS = 5000;

    private static ExecutorService pool;

    @BeforeAll
    static void init() {
        if (MULTITHREADED_TESTING) {
            pool = Executors.newCachedThreadPool();
        } else {
            pool = Executors.newSingleThreadExecutor();
        }
    }

    @Test
    void simulate_noDeadlocks_acceptableFairness() throws ExecutionException, InterruptedException, TimeoutException {
        Queue<Double> deltas = new ConcurrentLinkedQueue<>();

        List<Future<Void>> tasks = new LinkedList<>();
        Future<Void> task;
        for (int i = 0; i < SIMULATION_COUNT; i++) {

            task = pool.submit(() -> {
                Simulation simulation = new Simulation();
                Map<Integer, Integer> results = Assertions.assertTimeoutPreemptively(
                        Duration.ofMillis(SIMULATION_TIMEOUT_MILLIS),
                        simulation::simulate
                );

                Collection<Integer> values = results.values();
                int min = Collections.min(values);
                int max = Collections.max(values);
                double delta = ((double) max - (double) min) / (double) min * 100;
                double avg = values.stream().mapToInt(Integer::intValue).average().orElse(-1);
                double median;
                var sortedStream = values.stream().mapToInt(Integer::intValue).sorted();
                if (values.size() % 2 == 0) {
                    median = sortedStream.skip((values.size() / 2) - 1).limit(2).average().orElse(-1);
                } else {
                    median = sortedStream.skip(values.size() / 2).limit(1).findFirst().orElse(-1);
                }
                log.info("Min: {} | Max : {} | Delta (%): {} | Average : {} | Median : {}",
                        "%6d".formatted(min),
                        "%6d".formatted(max),
                        "%5.2f".formatted(delta),
                        "%.2f".formatted(avg),
                        median);

                Assertions.assertTrue(delta <= ACCEPTABLE_FAIRNESS_DELTA_PERCENT);

                deltas.add(delta);
                return null;
            });

            tasks.add(task);
        }

        while (!tasks.isEmpty()) {
            task = tasks.removeFirst();
            task.get(SIMULATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
        }

        log.info("Average delta (%): {}",
                "%.2f".formatted(
                        deltas.stream().mapToDouble(Double::doubleValue).average().orElse(-1))
        );
    }
}
