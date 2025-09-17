package org.labs;

import lombok.extern.slf4j.Slf4j;
import org.labs.util.Formatter;

import java.util.Map;

@Slf4j
public class Main {
    public static void main(String[] args) throws InterruptedException {
        Simulation simulation = new Simulation();
        Map<Integer, Integer> simulationResult = simulation.simulate();
        log.debug("Simulation results:{}", Formatter.formatSimulationResultMap(simulationResult));
    }
}
