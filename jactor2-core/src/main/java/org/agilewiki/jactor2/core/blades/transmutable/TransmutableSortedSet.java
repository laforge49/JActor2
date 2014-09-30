package org.agilewiki.jactor2.core.blades.transmutable;

import java.util.*;

/**
 * A transmutable tree set.
 */
public class TransmutableSortedSet<E> extends TreeSet<E> implements Transmutable<SortedSet<E>> {
    public TransmutableSortedSet() {
    }

    public TransmutableSortedSet(Comparator<? super E> comparator) {
        super(comparator);
    }

    public TransmutableSortedSet(Set<E> set) {
        super(set);
    }

    public TransmutableSortedSet(SortedSet<E> set) {
        super(set);
    }

    @Override
    public SortedSet<E> createUnmodifiable() {
        return Collections.unmodifiableSortedSet(new TreeSet<E>(this));
    }

    @Override
    public TransmutableSortedSet<E> recreate(SortedSet<E> unmodifiable) {
        return new TransmutableSortedSet(unmodifiable);
    }
}
