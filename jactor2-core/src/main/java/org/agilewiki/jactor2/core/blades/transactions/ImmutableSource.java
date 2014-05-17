package org.agilewiki.jactor2.core.blades.transactions;

/**
 * A source of an immutable object.
 */
public interface ImmutableSource<IMMUTABLE> {

    /**
     * Returns an immutable data structure.
     *
     * @return The immutable data structure.
     */
    IMMUTABLE getImmutable();
}
