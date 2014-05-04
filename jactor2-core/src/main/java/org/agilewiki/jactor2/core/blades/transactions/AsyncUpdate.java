package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public interface AsyncUpdate<IMMUTABLE> {
    void update(ImmutableReference<IMMUTABLE> source,
                ImmutableReference<IMMUTABLE> target,
                AsyncResponseProcessor<IMMUTABLE> asyncResponseProcessor) throws Exception;
}
