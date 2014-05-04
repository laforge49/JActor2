package org.agilewiki.jactor2.core.blades.transactions;

public interface SyncUpdate<IMMUTABLE> {
    IMMUTABLE update(ImmutableReference<IMMUTABLE> source, ImmutableReference<IMMUTABLE> target) throws Exception;
}
