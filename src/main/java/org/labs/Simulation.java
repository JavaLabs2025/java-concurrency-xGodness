package org.labs;

import org.labs.model.Programmer;
import org.labs.model.Spoon;
import org.labs.model.Waiter;

import java.util.*;
import java.util.stream.Collectors;

public class Simulation {
    private static final int PROGRAMMERS_COUNT = Constants.PROGRAMMERS_COUNT.getValue();
    private static final int WAITERS_COUNT = Constants.WAITERS_COUNT.getValue();

    public Map<Integer, Integer> simulate() throws InterruptedException {
        Context.init();
        List<Thread> activeThreads = new LinkedList<>();
        initializeWaiters(activeThreads);
        Programmer[] programmers = initializeProgrammers(activeThreads);

        for (Thread t : activeThreads) {
            t.join();
        }

        return Arrays.stream(programmers)
                .collect(Collectors.toMap(Programmer::getId, Programmer::getConsumed));
    }

    private static void initializeWaiters(List<Thread> activeThreads) {
        Waiter[] waiters = new Waiter[WAITERS_COUNT];
        Thread thread;
        for (int i = 0; i < WAITERS_COUNT; i++) {
            waiters[i] = new Waiter(i);
            thread = new Thread(waiters[i]);
            activeThreads.add(thread);
            thread.start();
        }
    }

    private static Programmer[] initializeProgrammers(List<Thread> activeThreads) {
        Spoon[] spoons = initializeSpoons();

        Programmer[] programmers = new Programmer[PROGRAMMERS_COUNT];
        Thread thread;
        for (int i = 0; i < PROGRAMMERS_COUNT; i++) {
            programmers[i] = new Programmer(i, spoons[i], spoons[(i + 1) % PROGRAMMERS_COUNT]);
            if (i % 2 == 0) {
                thread = new Thread(programmers[i]);
                activeThreads.add(thread);
                thread.start();
            }
        }

        for (int i = 1; i < PROGRAMMERS_COUNT; i += 2) {
            thread = new Thread(programmers[i]);
            activeThreads.add(thread);
            thread.start();
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
