package org.agilewiki.jactor2.core.requests;

/**
 * When a request throws a StackOverflowError,
 * a StackOverflowException is passed back to the originator of the request.
 */
public class StackOverflowException extends RuntimeException {
    /**
     * Create a StackOverflowException that wraps a StackOverflowError.
     *
     * @param soe The StackOverflowError.
     */
    public StackOverflowException(final StackOverflowError soe) {
        super(soe);
    }
}
