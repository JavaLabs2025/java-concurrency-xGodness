package org.labs.model;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.labs.Constants;
import org.labs.Context;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class Programmer implements Runnable {
    private static final int EATING_TIME_MIN_MILLIS = Constants.EATING_TIME_MIN_MILLIS.getValue();
    private static final int EATING_TIME_MAX_MILLIS = Constants.EATING_TIME_MAX_MILLIS.getValue();
    private static final int SIMULATION_SPEED_COEFFICIENT = Constants.SIMULATION_SPEED_COEFFICIENT.getValue();
    private static final int TIMEOUT_MILLIS = Constants.DEFAULT_TIMEOUT_MILLIS.getValue();

    private static final Random random = new Random();

    @Getter
    private final int id;
    @Getter
    private int consumed;

    private final Bowl bowl;
    private final Spoon first;
    private final Spoon second;


    public Programmer(int id, Spoon first, Spoon second) {
        this.id = id;
        this.bowl = new Bowl();
        if (first.compareTo(second) < 0) {
            this.first = first;
            this.second = second;
        } else {
            this.first = second;
            this.second = first;
        }
        consumed = 0;
    }


    @Override
    @SneakyThrows(InterruptedException.class)
    public void run() {
        while (true) {
            if (bowl.isEmpty()) {
                log.debug("Bowl {} is empty", id);
                if (!bowl.isQueued()) {
                    bowl.setIsQueued();
                    Context.getRefillQueue().put(bowl);
                }
                if (Context.isDinnerCompleted()) {
                    break;
                }
                continue;
            }

            synchronized (first.getLock()) {
                while (first.getLastUserId() == id) {
                    first.getLock().wait(TIMEOUT_MILLIS);
                }

                synchronized (second.getLock()) {
                    while (second.getLastUserId() == id) {
                        second.getLock().wait(TIMEOUT_MILLIS);
                    }

                    if (Context.isDinnerCompleted()) {
                        first.setLastUserId(id);
                        second.setLastUserId(id);
                        second.getLock().notify();
                        first.getLock().notify();
                        break;
                    }

                    consumed += eat();

                    second.setLastUserId(id);
                    second.getLock().notify();
                }

                first.setLastUserId(id);
                first.getLock().notify();
            }
        }
        log.debug("Programmer {} consumed {}", id, consumed);
    }

    private int eat() throws InterruptedException {
        log.debug("Programmer {} is eating now", id);
        int eatingTime = random.nextInt(
                EATING_TIME_MIN_MILLIS,
                EATING_TIME_MAX_MILLIS);
        eatingTime = Math.min(eatingTime, bowl.consume(eatingTime));
        TimeUnit.MILLISECONDS.sleep(eatingTime / SIMULATION_SPEED_COEFFICIENT);
        return eatingTime;
    }
}
