package org.agilewiki.jactor.util.durable;

import org.agilewiki.jactor.util.durable.incDes.IncDes;

/**
 * All incrementally deserializable objects must implement this interface
 * and return a reference to their durable part.
 */
public interface JASerializable {
    /**
     * Returns a non-null reference to the objects durable part.
     *
     * @return The durable part of a serializable object.
     */
    IncDes getDurable();
}
