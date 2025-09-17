package org.labs;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.*;

@Slf4j
public class SimulationTest {
    private static final int SIMULATION_COUNT = 15;
    private static final int ACCEPTABLE_FAIRNESS_DELTA_PERCENT = 30;

    private static Simulation simulation;

    @BeforeAll
    static void init() {
        simulation = new Simulation();
    }

    @Test
    void simulate_noDeadlocks_acceptableFairness() throws InterruptedException {
        Map<Integer, Integer> results;
        Collection<Integer> values;
        List<Double> deltaList = new LinkedList<>();
        int min;
        int max;
        double delta;
        double avg;
        double median;
        for (int i = 0; i < SIMULATION_COUNT; i++) {
            results = simulation.simulate();
            values = results.values();
            min = Collections.min(values);
            max = Collections.max(values);
            delta = ((double) max - (double) min) / (double) min * 100;
            avg = values.stream().mapToInt(Integer::intValue).average().orElse(-1);
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
            deltaList.add(delta);
        }

        log.info("Average delta (%): {}",
                "%.2f".formatted(
                        deltaList.stream().mapToDouble(Double::doubleValue).average().orElse(-1))
        );
    }
}
