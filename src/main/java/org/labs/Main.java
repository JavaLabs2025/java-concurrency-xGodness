package org.labs;

public class Main {
    private static final int PROGRAMMERS_COUNT = 7;

    public static void main(String[] args) {
        Spoon[] spoons = new Spoon[PROGRAMMERS_COUNT];
        for (int i = 0; i < PROGRAMMERS_COUNT; i++) {
            spoons[i] = new Spoon(i);
        }

        Programmer[] programmers = new Programmer[PROGRAMMERS_COUNT];
        for (int i = 0; i < PROGRAMMERS_COUNT; i++) {
            programmers[i] = new Programmer(i, spoons[i], spoons[(i + 1) % PROGRAMMERS_COUNT]);
            if (i % 2 == 0) {
                new Thread(programmers[i]).start();
            }
        }

        for (int i = 1; i < PROGRAMMERS_COUNT; i += 2) {
            new Thread(programmers[i]).start();
        }
    }
}
