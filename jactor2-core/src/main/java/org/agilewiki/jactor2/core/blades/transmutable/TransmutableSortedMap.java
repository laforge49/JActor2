package org.agilewiki.jactor2.core.blades.transmutable;

import java.util.*;

/**
 * A transmutable sorted map.
 */
public class TransmutableSortedMap<K, V> extends TreeMap<K, V> implements Transmutable<Map<K, V>> {
    protected volatile SortedMap<K, V> unmodifiable;

    public TransmutableSortedMap() {
        createUnmodifiable();
    }

    public TransmutableSortedMap(Comparator<? super K> comparator) {
        super(comparator);
        createUnmodifiable();
    }

    public TransmutableSortedMap(Map<K, V> map) {
        super(map);
        createUnmodifiable();
    }

    public TransmutableSortedMap(SortedMap<K, V> map) {
        super(map);
        createUnmodifiable();
    }

    @Override
    public Map<K, V> getUnmodifiable() {
        return unmodifiable;
    }

    @Override
    public void createUnmodifiable() {
        unmodifiable = Collections.unmodifiableSortedMap(new TreeMap(this));
    }

    @Override
    public Transmutable<Map<K, V>> recover() {
        return new TransmutableSortedMap<K, V>(unmodifiable);
    }
}
