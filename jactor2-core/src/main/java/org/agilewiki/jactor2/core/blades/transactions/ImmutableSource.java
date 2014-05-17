package org.agilewiki.jactor2.core.blades.transactions;

/**
 * A source of an immutable object.
 */
public interface ImmutableSource<IMMUTABLE> {

    /**
     * Returns AN immutable data structure.
     *
     * @return The immutable data structure.
     */
    IMMUTABLE getImmutable();
}
