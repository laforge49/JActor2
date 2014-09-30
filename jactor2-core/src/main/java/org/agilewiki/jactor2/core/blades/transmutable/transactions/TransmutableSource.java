package org.agilewiki.jactor2.core.blades.transmutable.transactions;

import org.agilewiki.jactor2.core.blades.transmutable.Transmutable;

/**
 * Provides a reference to a transutable.
 */
public abstract class TransmutableSource<DATATYPE, TRANSMUTABLE extends Transmutable<DATATYPE>> {
    /**
     * Returns the transmutable.
     *
     * @return The transmutable.
     */
    abstract protected TRANSMUTABLE getTransmutable();
}
