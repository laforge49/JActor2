package org.agilewiki.jactor2.core.blades.transmutable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A transmutabe set.
 */
public class TransmutableSet<E> extends HashSet<E> implements Transmutable<Set<E>> {
    protected volatile Set<E> unmodifiable;

    public TransmutableSet() {
        createUnmodifiable();
    }

    public TransmutableSet(int size) {
        super(size);
        createUnmodifiable();
    }

    public TransmutableSet(Set<E> set) {
        super(set);
        createUnmodifiable();
    }

    @Override
    public Set<E> getUnmodifiable() {
        return unmodifiable;
    }

    @Override
    public void createUnmodifiable() {
        unmodifiable = Collections.unmodifiableSet(new HashSet<E>(this));
    }

    @Override
    public Transmutable<Set<E>> recover() {
        return new TransmutableSet(unmodifiable);
    }
}
