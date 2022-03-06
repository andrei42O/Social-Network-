package com.socialnetwork.domain;

import java.util.Objects;

public class Pair<T1, T2> {
    private T1 first;
    private T2 second;

    /**
     * Pair class is a pair in which thwe order does not matter
     * @param first
     * @param second
     */
    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }

    /**
     * getter
     * @return the first object
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * getter
     * @return the second object
     */
    public T2 getSecond() {
        return second;
    }

    /**
     * (x, y) = (x, y)
     * (x, y) = (y, x)
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair pair = (Pair) o;
        return ((Objects.equals(first, pair.first) && Objects.equals(second, pair.second)) || (Objects.equals(first, pair.second) && Objects.equals(second, pair.first)));
    }

    @Override
    public int hashCode() {
        int hashFirst = first.hashCode();
        int hashSecond = second.hashCode();
        int maxHash = Math.max(hashFirst, hashSecond);
        int minHash = Math.min(hashFirst, hashSecond);
        return minHash * 31 + maxHash;
    }
}