package org.agilewiki.jactor2.core.plant;

/**
 * This exception is thrown when sending a AsyncRequest to a different facility and that facility is closed.
 * This exception is also thrown when closing a facility that is processing a request from a different facility.
 * This becomes important when working with OSGi and each bundle has its own lifecycle.
 */
public class ServiceClosedException extends Exception {

    /**  */
    private static final long serialVersionUID = 1L;
}
