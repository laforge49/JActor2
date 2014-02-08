package org.agilewiki.jactor2.core.requests;

/**
 * When a request throws a StackOverflowError,
 * a StackOverflowException is passed back to the originator of the request.
 */
public class StackOverflowException extends RuntimeException {
}
