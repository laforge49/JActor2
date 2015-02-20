package org.agilewiki.jactor2.core.messages;

/**
 * An operation, synchronous or asynchronous, optionally used to define requests.
 */
public interface Operation<RESPONSE_TYPE> {
    String getOpName();
}
