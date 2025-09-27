package org.labs;

import lombok.extern.slf4j.Slf4j;
import org.labs.util.Formatter;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

@Slf4j
public class Main {
    private static final long SIMULATION_TIMEOUT_MILLIS = 7_500;

    public static void main(String[] args) {
        try (ExecutorService pool = Executors.newCachedThreadPool()) {
            Simulation simulation = new Simulation();
            try {
                Map<Integer, Integer> simulationResult = simulation.simulate(pool, SIMULATION_TIMEOUT_MILLIS);
                log.debug("Simulation results:{}", Formatter.formatSimulationResultMap(simulationResult));
            } catch (InterruptedException | TimeoutException ex) {
                log.error(ex.getMessage());
                pool.shutdownNow();
            }
        }

    }
}
