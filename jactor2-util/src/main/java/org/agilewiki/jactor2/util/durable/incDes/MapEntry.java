package org.agilewiki.jactor2.util.durable.incDes;

/**
 * Serializable map entry.
 *
 * @param <KEY_TYPE>   Type of key.
 * @param <VALUE_TYPE> Type of Value.
 */
public interface MapEntry<KEY_TYPE extends Comparable<KEY_TYPE>, VALUE_TYPE>
        extends IncDes {

    /**
     * Returns the key.
     *
     * @return The key.
     */
    KEY_TYPE getKey() throws Exception;

    /**
     * Returns the value.
     *
     * @return The value.
     */
    VALUE_TYPE getValue() throws Exception;
}
