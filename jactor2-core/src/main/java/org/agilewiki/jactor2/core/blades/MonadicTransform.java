package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.requests.AsyncResponseProcessor;

public interface MonadicTransform<Immutable> {
    void t(BladeMonad<Immutable> bladeMonad, AsyncResponseProcessor<Immutable> asyncResponseProcessor);
}