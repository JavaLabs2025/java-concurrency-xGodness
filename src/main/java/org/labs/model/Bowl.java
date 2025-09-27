package org.labs.model;

import org.labs.Constants;

import java.util.concurrent.locks.ReentrantLock;

public class Bowl {
    private final ReentrantLock lock;
    private int contents;
    private boolean isQueued;

    public Bowl() {
        this.lock = new ReentrantLock();
        this.contents = Constants.BOWL_CAPACITY.getValue();
        this.isQueued = false;
    }

    public int consume(int amount) {
        try {
            lock.lock();
            int consumed = Math.min(amount, contents);
            contents -= consumed;
            return consumed;
        } finally {
            lock.unlock();
        }
    }

    public void refill(int amount) {
        try {
            lock.lock();
            contents = amount;
            isQueued = false;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        try {
            lock.lock();
            return contents == 0;
        } finally {
            lock.unlock();
        }
    }

    public boolean isQueued() {
        try {
            lock.lock();
            return isQueued;
        } finally {
            lock.unlock();
        }
    }

    public void setIsQueued() {
        try {
            lock.lock();
            isQueued = true;
        } finally {
            lock.unlock();
        }
    }
}
