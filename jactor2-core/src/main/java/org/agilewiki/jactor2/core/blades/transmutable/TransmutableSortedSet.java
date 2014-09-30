package org.agilewiki.jactor2.core.blades.transmutable;

import java.util.*;

/**
 * A transmutable tree set.
 */
public class TransmutableSortedSet<E> extends TreeSet<E> implements Transmutable<SortedSet<E>> {
    protected volatile SortedSet<E> unmodifiable;

    public TransmutableSortedSet() {
        createUnmodifiable();
    }

    public TransmutableSortedSet(Comparator<? super E> comparator) {
        super(comparator);
        createUnmodifiable();
    }

    public TransmutableSortedSet(Set<E> set) {
        super(set);
        createUnmodifiable();
    }

    public TransmutableSortedSet(SortedSet<E> set) {
        super(set);
        createUnmodifiable();
    }

    @Override
    public SortedSet<E> getUnmodifiable() {
        return unmodifiable;
    }

    @Override
    public void createUnmodifiable() {
        unmodifiable = Collections.unmodifiableSortedSet(new TreeSet<E>(this));
    }

    @Override
    public Transmutable<SortedSet<E>> recover() {
        return new TransmutableSet(unmodifiable);
    }
}
