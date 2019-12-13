package com.scalablecapital;

import java.util.*;
import java.util.stream.Collectors;

public class TopNStorage<T extends Comparable<T>> {

    private final int n;
    private final Map<T, Integer> store = new HashMap<>();

    public TopNStorage(int n) {
        this.n = n;
    }

    public synchronized void add(T element) {
        store.compute(element, (elem, count) -> {
            if (count == null) {
                return 1;
            } else {
                return count + 1;
            }
        });
    }

    public synchronized List<String> getTop() {
        return store.entrySet().stream()
                .sorted(Comparator.comparingInt(Map.Entry::getValue))
                .limit(n)
                .map(e -> "js='" + e.getKey() + "\t=\t" + e.getValue()+"\n")
                .collect(Collectors.toList());
    }

    public synchronized Map<T, Integer> getStore() {
        return store;
    }

}
