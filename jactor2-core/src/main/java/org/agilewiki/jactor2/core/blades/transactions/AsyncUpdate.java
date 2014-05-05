package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

/**
 * An asynchronous operation to be applied to an ImmutableReference.
 *
 * @param <IMMUTABLE>    The type of immutable data structure.
 */
public interface AsyncUpdate<IMMUTABLE> {
    /**
     * Updates the immutable data structure.
     *
     * @param source    The Transaction or ImmutableReference holding the immutable to be operated on.
     * @param target    The transaction where the updates will be saved.
     * @param asyncResponseProcessor    Updates the immutable in the target transaction.
     */
    void update(ImmutableReference<IMMUTABLE> source,
                ImmutableReference<IMMUTABLE> target,
                AsyncResponseProcessor<IMMUTABLE> asyncResponseProcessor) throws Exception;
}
