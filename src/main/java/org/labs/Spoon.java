package org.labs;

import lombok.Getter;
import lombok.Setter;

public class Spoon implements Comparable<Spoon> {
    private final int id;
    @Getter
    @Setter
    private int lastUserId;

    public Spoon(int id) {
        this.id = id;
        this.lastUserId = -1;
    }

    @Override
    public int compareTo(Spoon spoon) {
        return Integer.compare(id, spoon.id);
    }
}
