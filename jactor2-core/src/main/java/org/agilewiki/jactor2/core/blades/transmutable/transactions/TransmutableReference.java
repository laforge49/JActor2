package org.agilewiki.jactor2.core.blades.transmutable.transactions;

import org.agilewiki.jactor2.core.blades.IsolationBlade;
import org.agilewiki.jactor2.core.blades.transmutable.Transmutable;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.NonBlockingReactor;

/**
 * An IsolationBlade to which transactions can be applied.
 */
public class TransmutableReference<DATATYPE, TRANSMUTABLE extends Transmutable<DATATYPE>>
        implements IsolationBlade, Transmutable<DATATYPE>, TransmutableSource<DATATYPE, TRANSMUTABLE> {

    /**
     * The blade's reactor.
     */
    protected IsolationReactor reactor;

    /**
     * The transmutable to be operated on.
     */
    private TRANSMUTABLE transmutable;

    /**
     * Create an ImmutableReference blade.
     *
     * @param _transmutable The transmutable data structure to be operated on.
     */
    public TransmutableReference(final TRANSMUTABLE _transmutable) throws Exception {
        reactor = new IsolationReactor();
        transmutable = _transmutable;
    }

    /**
     * Create an ImmutableReference blade.
     *
     * @param _transmutable The transmutable data structure to be operated on.
     * @param _reactor      The blade's reactor.
     */
    public TransmutableReference(final TRANSMUTABLE _transmutable,
                                 final IsolationReactor _reactor) {
        reactor = _reactor;
        transmutable = _transmutable;
    }

    /**
     * Create an ImmutableReference blade.
     *
     * @param _transmutable  The transmutable data structure to be operated on.
     * @param _parentReactor The parent of the blade's reactor.
     */
    public TransmutableReference(final TRANSMUTABLE _transmutable,
                                 final NonBlockingReactor _parentReactor) throws Exception {
        reactor = new IsolationReactor(_parentReactor);
        transmutable = _transmutable;
    }

    @Override
    public IsolationReactor getReactor() {
        return null;
    }

    @Override
    public TRANSMUTABLE getTransmutable() {
        return transmutable;
    }

    @Override
    public DATATYPE getUnmodifiable() {
        return transmutable.getUnmodifiable();
    }

    @Override
    public void createUnmodifiable() {
        transmutable.createUnmodifiable();
    }

    @Override
    public Transmutable<DATATYPE> recover() {
        return transmutable.recover();
    }
}
