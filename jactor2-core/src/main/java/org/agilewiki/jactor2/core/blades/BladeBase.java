package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.messages.*;
import org.agilewiki.jactor2.core.plant.impl.PlantImpl;
import org.agilewiki.jactor2.core.reactors.Reactor;

/**
 * Optional base class for blades.
 */
public abstract class BladeBase implements Blade {
    private static volatile int nextHash;
    /**
     * The blade's targetReactor.
     */
    private Reactor reactor;
    /** Our hashcode. */
    private final int hashCode = nextHash++;

    /** Redefines the hashcode for a faster hashing. */
    @Override
    public int hashCode() {
        return hashCode;
    }

    /**
     * Returns true when the blade has been initialized.
     *
     * @return True when the blade has been initialized.
     */
    public boolean isInitialized() {
        return (reactor != null);
    }

    /**
     * Initialize a blade. This method can only be called once
     * without raising an illegal state exception, as the targetReactor
     * can not be changed.
     *
     * @param _reactor The blade's targetReactor.
     */
    protected void _initialize(final Reactor _reactor) {
        if (reactor != null) {
            throw new IllegalStateException("Already initialized " + this);
        }
        if (_reactor == null) {
            throw new IllegalArgumentException("Reactor may not be null");
        }
        reactor = _reactor;
    }

    @Override
    public Reactor getReactor() {
        return reactor;
    }

    /**
     * Send a one-way message using the blade's reactor as the source.
     *
     * @param _aOp        The operation to be processed.
     */
    protected <RESPONSE_TYPE> void send(final AOp<RESPONSE_TYPE> _aOp) {
        PlantImpl.getSingleton()
                .createAsyncRequestImpl(_aOp, _aOp.targetReactor)
                .doSend(getReactor().asReactorImpl(), null);
    }

    public boolean isDirectOk(final Reactor _sourceReactor) {
        if (!reactor.asReactorImpl().isRunning()) {
            throw new IllegalStateException(
                    "Not thread safe: not called from within an active request");
        }
        return reactor == _sourceReactor;
    }

    /**
     * Validate that the source reactor is the same as the target and that the source reactor is active.
     *
     * @param _sourceReactor    The source reactor.
     */
    public void directCheck(final Reactor _sourceReactor) {
        if (!isDirectOk(_sourceReactor)) {
            throw new UnsupportedOperationException(
                    "Not thread safe: source reactor is not the same");
        }
    }

    public abstract class AReq<RESPONSE_TYPE> extends AOp<RESPONSE_TYPE> {
        public AReq(final String _opName) {
            super(_opName, getReactor());
        }
    }

    public abstract class ASig extends AIOp<Void> {
        public ASig(final String _opName) {
            super(_opName, getReactor());
        }
    }

    public abstract class OAReq<RESPONSE_TYPE> extends SAOp<RESPONSE_TYPE> {
        public OAReq(final String _opName) {
            super(_opName, getReactor());
        }
    }

    public abstract class SReq<RESPONSE_TYPE> extends SOp<RESPONSE_TYPE> {
        public SReq(final String _opName) {
            super(_opName, getReactor());
        }
    }

    public abstract class SSig extends SIOp<Void> {
        public SSig(final String _opName) {
            super(_opName, getReactor());
        }
    }
}
