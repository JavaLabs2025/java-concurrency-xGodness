package org.labs;

import lombok.SneakyThrows;

public class Programmer implements Runnable {
    private static final long LUNCH_TIME_MILLIS = 10_000;
    private static final int EATING_TIME_MILLIS = 1_000;

    private final int id;
    private final long startTimeMillis;
    private final Spoon first;
    private final Spoon second;
    private int consumed;

    public Programmer(int id, Spoon first, Spoon second) {
        this.id = id;
        this.startTimeMillis = System.currentTimeMillis();
        if (first.compareTo(second) < 0) {
            this.first = first;
            this.second = second;
        } else {
            this.first = second;
            this.second = first;
        }
        consumed = 0;
    }

    @SneakyThrows
    @Override
    public void run() {
        while (true) {
            synchronized (first) {
                if (first.getLastUserId() == id) {
                    first.wait();
                }

                synchronized (second) {
                    if (second.getLastUserId() == id) {
                        second.wait();
                    }

                    if (System.currentTimeMillis() - startTimeMillis > LUNCH_TIME_MILLIS) {
                        first.setLastUserId(id);
                        second.setLastUserId(id);
                        second.notify();
                        first.notify();
                        break;
                    }

                    eat();
                    consumed += EATING_TIME_MILLIS;

                    second.setLastUserId(id);
                    second.notify();
                }

                first.setLastUserId(id);
                first.notify();
            }
        }
        System.out.println("Programmer " + id + " consumed " + consumed);
    }

    private void eat() throws InterruptedException {
        System.out.println("Programmer " + id + " is eating now");
        Thread.sleep(EATING_TIME_MILLIS);
    }
}
