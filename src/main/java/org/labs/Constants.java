package org.labs;

public enum Constants {
    SIMULATION_SPEED_COEFFICIENT(150),
    DEFAULT_TIMEOUT_MILLIS(500),

    PROGRAMMERS_COUNT(7),
    WAITERS_COUNT(2),

    INITIAL_FOOD_AMOUNT(1_000_000),
    BOWL_CAPACITY(2_000),

    EATING_TIME_MIN_MILLIS(250),
    EATING_TIME_MAX_MILLIS(500);


    private final int val;

    Constants(int val) {
        this.val = val;
    }

    public int getValue() {
        return val;
    }
}
