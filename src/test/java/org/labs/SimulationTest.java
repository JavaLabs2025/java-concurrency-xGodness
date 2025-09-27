package org.labs;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class SimulationTest {
    private static final int SIMULATION_COUNT = 15;
    private static final int ACCEPTABLE_FAIRNESS_DELTA_PERCENT = 30;
    private static final int SIMULATION_TIMEOUT_MILLIS = 7_500;

    private static ExecutorService pool;

    @BeforeAll
    static void init() {
        pool = Executors.newCachedThreadPool();
    }

    @Test
    void simulate_noDeadlocks_acceptableFairness() throws InterruptedException {
        Queue<Double> deltas = new ConcurrentLinkedQueue<>();

        List<Future<?>> tasks = new LinkedList<>();
        Future<?> task;
        for (int i = 0; i < SIMULATION_COUNT; i++) {

            task = pool.submit(() -> {
                Simulation simulation = new Simulation();

                Map<Integer, Integer> results = null;
                Throwable throwable = null;
                try {
                    results = simulation.simulate(pool, SIMULATION_TIMEOUT_MILLIS);
                } catch (InterruptedException | TimeoutException ex) {
                    log.error(ex.getMessage());
                    throwable = ex;
                }
                Assertions.assertNull(throwable);

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
            });

            tasks.add(task);
        }

        while (!tasks.isEmpty()) {
            task = tasks.removeFirst();
            try {
                task.get(SIMULATION_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            } catch (TimeoutException | ExecutionException ex) {
                log.error(ex.getMessage());
                Assertions.fail(ex);
            }
        }

        log.info("Average delta (%): {}",
                "%.2f".formatted(
                        deltas.stream().mapToDouble(Double::doubleValue).average().orElse(-1))
        );
    }
}
