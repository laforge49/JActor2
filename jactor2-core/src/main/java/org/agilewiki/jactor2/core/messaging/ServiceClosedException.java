package org.agilewiki.jactor2.core.messaging;

/**
 * This exception is thrown when sending a Request to a different context and that context is closed.
 * This becomes important when working with OSGi and each bundle has its own lifecycle.
 */
public class ServiceClosedException extends Exception {
}
