package org.agilewiki.jactor2.core.blades.transmutable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A transmutabe set.
 */
public class TransmutableSet<E> extends HashSet<E> implements Transmutable<Set<E>> {
    public TransmutableSet() {
    }

    public TransmutableSet(int size) {
        super(size);
    }

    public TransmutableSet(Set<E> set) {
        super(set);
    }

    @Override
    public Set<E> createUnmodifiable() {
        return Collections.unmodifiableSet(new HashSet<E>(this));
    }

    @Override
    public TransmutableSet<E> recreate(Set<E> unmodifiable) {
        return new TransmutableSet(unmodifiable);
    }
}
