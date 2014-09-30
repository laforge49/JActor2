package org.agilewiki.jactor2.core.blades.transmutable.transactions;

import org.agilewiki.jactor2.core.blades.transmutable.Transmutable;

/**
 * Source of a transmutable object.
 */
public interface TransmutableSource<DATATYPE, TRANSMUTABLE extends Transmutable<DATATYPE>> {
    /**
     * Returns the transmutable;
     *
     * @return The transmutavle, or null.
     */
    TRANSMUTABLE getTransmutable();
}
