package org.labs;

import lombok.Getter;
import org.labs.model.Bowl;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Context {
    private boolean isDinnerCompleted;
    private final BlockingQueue<Bowl> refillQueue;
    private final AtomicInteger remainingFood;

    public Context() {
        isDinnerCompleted = false;
        refillQueue = new LinkedBlockingQueue<>();
        remainingFood = new AtomicInteger(Constants.INITIAL_FOOD_AMOUNT.getValue());
    }

    public void completeDinner() {
        isDinnerCompleted = true;
    }
}
