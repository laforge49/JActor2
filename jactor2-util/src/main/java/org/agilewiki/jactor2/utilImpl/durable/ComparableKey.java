package org.agilewiki.jactor2.utilImpl.durable;

/**
 * Compares the key or value of a IncDesImpl.
 */
public interface ComparableKey<KEY_TYPE> {
    /**
     * Compares the key or value;
     *
     * @param o The comparison value.
     * @return The result of a compareTo(o).
     */
    public int compareKeyTo(KEY_TYPE o) throws Exception;
}
