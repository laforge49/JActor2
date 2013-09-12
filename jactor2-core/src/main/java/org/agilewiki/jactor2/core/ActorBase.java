package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.Reactor;

/**
 * <p>
 * ActorBase is a convenience class that implements an Actor. Initialization is not
 * thread-safe, so it should be done before a reference to the actor is shared.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class ActorBaseSample extends ActorBase {
 *     public ActorBaseSample(final Reactor _messageProcessor) throws Exception {
 *         initialize(_messageProcessor);
 *     }
 * }
 * </pre>
 */
public class ActorBase implements Actor {
    /**
     * The actor's reactor.
     */
    private Reactor reactor;

    /**
     * True when initialized, this flag is used to prevent the reactor from being changed.
     */
    private boolean initialized;

    /**
     * Returns true when the actor has been initialized.
     *
     * @return True when the actor has been initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Initialize an actor. This method can only be called once
     * without raising an illegal state exception, as the reactor
     * can not be changed.
     *
     * @param _reactor The actor's reactor.
     */
    public void initialize(final Reactor _reactor) throws Exception {
        if (initialized)
            throw new IllegalStateException("Already initialized");
        if (_reactor == null)
            throw new IllegalArgumentException("Reactor may not be null");
        initialized = true;
        reactor = _reactor;
    }

    @Override
    public Reactor getReactor() {
        return reactor;
    }
}
