package org.agilewiki.jactor2.core.blades.transactions;

/**
 * A synchronous operation to be applied to an ImmutableReference.
 *
 * @param <IMMUTABLE>    The type of immutable data structure.
 */
public interface SyncUpdate<IMMUTABLE> {
    /**
     * Updates the immutable data structure.
     *
     * @param source    The Transaction or ImmutableReference holding the immutable to be operated on.
     * @param target    The transaction where the updates will be saved.
     * @return The updated immutable.
     */
    IMMUTABLE update(ImmutableReference<IMMUTABLE> source, ImmutableReference<IMMUTABLE> target) throws Exception;
}
