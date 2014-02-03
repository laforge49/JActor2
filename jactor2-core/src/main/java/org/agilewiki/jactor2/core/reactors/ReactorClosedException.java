package org.agilewiki.jactor2.core.reactors;

/**
 * This exception is thrown when passing a Request to a reactor and that reactor is closed.
 * This exception is also thrown when closing a reactor that is processing a request from a different reactor.
 * This is important as a means of ensuring that every asynchronous request receives a response, even if that
 * response is an exception.
 */
public class ReactorClosedException extends RuntimeException {
}
