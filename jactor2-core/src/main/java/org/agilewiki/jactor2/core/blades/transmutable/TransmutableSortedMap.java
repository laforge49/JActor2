package org.agilewiki.jactor2.core.blades.transmutable;

import java.util.*;

/**
 * A transmutable sorted map.
 */
public class TransmutableSortedMap<K, V> extends TreeMap<K, V> implements Transmutable<SortedMap<K, V>> {
    public TransmutableSortedMap() {
    }

    public TransmutableSortedMap(Comparator<? super K> comparator) {
        super(comparator);
    }

    public TransmutableSortedMap(Map<K, V> map) {
        super(map);
    }

    public TransmutableSortedMap(SortedMap<K, V> map) {
        super(map);
    }

    @Override
    public SortedMap<K, V> createUnmodifiable() {
        return Collections.unmodifiableSortedMap(new TreeMap(this));
    }

    @Override
    public TransmutableSortedMap<K, V> recreate(SortedMap<K, V> unmodifiable) {
        return new TransmutableSortedMap<K, V>(unmodifiable);
    }
}
