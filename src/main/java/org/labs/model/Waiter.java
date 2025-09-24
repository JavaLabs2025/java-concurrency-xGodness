package org.labs.model;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.labs.Constants;
import org.labs.Context;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Waiter implements Runnable {
    private static final int BOWL_CAPACITY = Constants.BOWL_CAPACITY.getValue();
    private static final int TIMEOUT_MILLIS = Constants.DEFAULT_TIMEOUT_MILLIS.getValue();

    private final Context context;
    private final int id;

    public Waiter(Context context, int id) {
        this.context = context;
        this.id = id;
    }

    @Override
    @SneakyThrows(InterruptedException.class)
    public void run() {
        while (!context.isDinnerCompleted()) {
            Bowl bowl = context.getRefillQueue().poll(TIMEOUT_MILLIS, TimeUnit.MILLISECONDS);
            if (bowl == null) {
                continue;
            }

            int refillAmount = context.getRemainingFood().getAndUpdate(
                            (val) -> {
                                if (val < BOWL_CAPACITY) {
                                    return 0;
                                }
                                return val - BOWL_CAPACITY;
                            }
                    );
            if (refillAmount <= BOWL_CAPACITY) {
                context.completeDinner();
            }
            refillAmount = Math.min(refillAmount, BOWL_CAPACITY);
            bowl.refill(refillAmount);
        }
        log.debug("Waiter {} completed their work", id);
    }
}
