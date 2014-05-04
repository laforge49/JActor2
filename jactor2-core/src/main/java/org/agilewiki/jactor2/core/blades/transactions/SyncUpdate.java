package org.agilewiki.jactor2.core.blades.transactions;

public interface SyncUpdate<Immutable> {
    Immutable update(ImmutableReference<Immutable> source, ImmutableReference<Immutable> target);
}
