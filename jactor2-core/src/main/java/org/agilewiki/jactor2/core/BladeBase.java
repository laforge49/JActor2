package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.processing.Reactor;

/**
 * <p>
 * BladeBase is a convenience class that implements an Blade. Initialization is not
 * thread-safe, so it should be done before a reference to the blade is shared.
 * </p>
 * <h3>Sample Usage:</h3>
 * <pre>
 * public class BladeBaseSample extends BladeBase {
 *     public BladeBaseSample(final Reactor _messageProcessor) throws Exception {
 *         initialize(_messageProcessor);
 *     }
 * }
 * </pre>
 */
public class BladeBase implements Blade {
    /**
     * The blade's reactor.
     */
    private Reactor reactor;

    /**
     * True when initialized, this flag is used to prevent the reactor from being changed.
     */
    private boolean initialized;

    /**
     * Returns true when the blade has been initialized.
     *
     * @return True when the blade has been initialized.
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Initialize a blade. This method can only be called once
     * without raising an illegal state exception, as the reactor
     * can not be changed.
     *
     * @param _reactor The blade's reactor.
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
