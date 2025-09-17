package org.labs.model;

import lombok.Getter;
import org.labs.Constants;

public class Spoon implements Comparable<Spoon> {
    private static final long LAST_USED_EXPIRATION_MILLIS = Constants.DEFAULT_TIMEOUT_MILLIS.getValue();

    private final int id;

    private int lastUserId;
    private long lastUpdateTime;

    @Getter
    private final Object lock;

    public Spoon(int id) {
        this.id = id;
        setLastUserId(-1);
        this.lock = new Object();
    }

    public void setLastUserId(int lastUserId) {
        this.lastUserId = lastUserId;
        lastUpdateTime = System.currentTimeMillis();
    }

    public int getLastUserId() {
        if (System.currentTimeMillis() - lastUpdateTime > LAST_USED_EXPIRATION_MILLIS) {
            setLastUserId(-1);
        }
        return lastUserId;
    }

    @Override
    public int compareTo(Spoon spoon) {
        return Integer.compare(id, spoon.id);
    }
}
