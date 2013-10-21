package org.agilewiki.jactor2.core.messages;

/**
 * When implemented by a request passed to an isolation reactor, the request is allowed to intervene in current request
 * processing, just as all requests can intervene in request processing with a non-blocking reactor.
 */
public class Preempt {
}
