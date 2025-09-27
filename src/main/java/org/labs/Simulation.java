package org.labs;

import lombok.extern.slf4j.Slf4j;
import org.labs.model.Programmer;
import org.labs.model.Spoon;
import org.labs.model.Waiter;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
public class Simulation {
    private static final int PROGRAMMERS_COUNT = Constants.PROGRAMMERS_COUNT.getValue();
    private static final int WAITERS_COUNT = Constants.WAITERS_COUNT.getValue();

    public Map<Integer, Integer> simulate(ExecutorService threadPool, long timeoutMillis) throws InterruptedException, TimeoutException {
        Context context = new Context();

        Queue<Future<?>> activeTasks = new LinkedList<>();
        initializeWaiters(threadPool, context, activeTasks);
        Programmer[] programmers = initializeProgrammers(threadPool, context, activeTasks);

        long start = System.currentTimeMillis();
        while (!activeTasks.isEmpty() && System.currentTimeMillis() - start < timeoutMillis) {
            if (activeTasks.peek().isDone()) {
                activeTasks.poll();
            }
        }

        if (System.currentTimeMillis() - start >= timeoutMillis) {
            throw new TimeoutException("Execution took too long: timeout reached");
        }

        return Arrays.stream(programmers)
                .collect(Collectors.toMap(Programmer::getId, Programmer::getConsumed));
    }

    private static void initializeWaiters(ExecutorService threadPool, Context context, Queue<Future<?>> activeTasks) {
        Waiter[] waiters = new Waiter[WAITERS_COUNT];
        for (int i = 0; i < WAITERS_COUNT; i++) {
            waiters[i] = new Waiter(context, i);
            activeTasks.add(threadPool.submit(waiters[i]));
        }
    }

    private static Programmer[] initializeProgrammers(ExecutorService threadPool, Context context, Queue<Future<?>> activeTasks) {
        Spoon[] spoons = initializeSpoons();

        Programmer[] programmers = new Programmer[PROGRAMMERS_COUNT];
        for (int i = 0; i < PROGRAMMERS_COUNT; i++) {
            programmers[i] = new Programmer(context, i, spoons[i], spoons[(i + 1) % PROGRAMMERS_COUNT]);
            if (i % 2 == 0) {
                activeTasks.add(threadPool.submit(programmers[i]));
            }
        }

        for (int i = 1; i < PROGRAMMERS_COUNT; i += 2) {
            activeTasks.add(threadPool.submit(programmers[i]));
        }

        return programmers;
    }

    private static Spoon[] initializeSpoons() {
        Spoon[] spoons = new Spoon[PROGRAMMERS_COUNT];
        for (int i = 0; i < PROGRAMMERS_COUNT; i++) {
            spoons[i] = new Spoon(i);
        }
        return spoons;
    }
}
