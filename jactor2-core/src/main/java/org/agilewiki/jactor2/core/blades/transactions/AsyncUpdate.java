package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public interface AsyncUpdate<Immutable> {
    void update(ImmutableReference<Immutable> source,
                ImmutableReference<Immutable> target,
                AsyncResponseProcessor<Immutable> asyncResponseProcessor);
}
