package org.labs;

import lombok.Getter;
import org.labs.model.Bowl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Context {
    @Getter
    private static boolean isDinnerCompleted = false;

    @Getter
    private static final BlockingQueue<Bowl> refillQueue = new LinkedBlockingQueue<>();

    @Getter
    private static final AtomicInteger remainingFood = new AtomicInteger(Constants.INITIAL_FOOD_AMOUNT.getValue());

    public static void init() {
        isDinnerCompleted = false;
        refillQueue.clear();
        remainingFood.set(Constants.INITIAL_FOOD_AMOUNT.getValue());
    }

    public static void completeDinner() {
        isDinnerCompleted = true;
    }
}
