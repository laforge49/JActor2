package org.agilewiki.jactor2.core.blades.transmutable;

/**
 * An object which supports an unmodifiable form.
 * The unmodifiable form can be used with complete thread-safety for queries,
 * while the creation of the unmodifiable form typically is not thread safe.
 *
 * @param <DATATYPE> The type of the modifiable form--typically an interface.
 */
public interface Transmutable<DATATYPE> {
    /**
     * Returns the last created unmodifiable form, if any.
     *
     * @return The last created unmodifiable form, or null.
     */
    DATATYPE getUnmodifiable();

    /**
     * Create a new unmodifyable form that reflects the latest changes until now.
     * But note that THIS IS NOT A VIEW
     */
    void createUnmodifiable();

    /**
     * Return a transmutable created from the last created unmodifiabe form, if any.
     *
     * @return A transmutable created from the last unmodifiabe form, or null.
     */
    Transmutable<DATATYPE> recover();
}
