package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.IsolationBlade;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * An IsolationBlade to which transactions can be applied.
 *
 * @param <IMMUTABLE> The type of immutable to be operated on.
 */
public class ImmutableReference<IMMUTABLE> implements IsolationBlade {
    /**
     * The blade's reactor.
     */
    protected IsolationReactor reactor;

    /**
     * The immutable data structure to be operated on.
     */
    protected IMMUTABLE immutable;

    /**
     * Create an ImmutableReference blade.
     *
     * @param _immutable    The immutable data structure to be operated on.
     */
    public ImmutableReference(final IMMUTABLE _immutable) {
        reactor = new IsolationReactor();
        immutable = _immutable;
    }

    /**
     * Create an ImmutableReference blade.
     *
     * @param _immutable    The immutable data structure to be operated on.
     * @param _parentReactor    The parent of the blade's reactor.
     */
    public ImmutableReference(final IMMUTABLE _immutable, final NonBlockingReactor _parentReactor) {
        reactor = new IsolationReactor(_parentReactor);
        immutable = _immutable;
    }

    ImmutableReference() {
    }

    public IsolationReactor getReactor() {
        return reactor;
    }

    /**
     * Returns the blade's immutable data structure.
     *
     * @return The Blade's immutable data structure.
     */
    public IMMUTABLE getImmutable() {
        return immutable;
    }
}
