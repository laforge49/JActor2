package org.agilewiki.jactor2.core;

import org.agilewiki.jactor2.core.messaging.AsyncRequest;

/**
 * An Agent is a blade with a StartAReq and which is dynamically added to a Reactor
 * to interact with the blades of that Reactor. One of the advantages of an Agent is
 * to reduce the number of requests that pass between threads. The other can be to perform
 * multiple local requests against the other blades in that reactor in isolation from other requests.
 * </p>
 * Any needed parameters are not passed to the startAReq, but are instead are typically
 * passed in the constructor of the subclasses of Agent.
 */
public interface Agent<RESPONSE_TYPE> extends Blade {
    abstract AsyncRequest<RESPONSE_TYPE> startAReq();
}
