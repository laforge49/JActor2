package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.messages.AsyncRequest;

/**
 * An AsyncAgent is a blade with a StartAReq and which is dynamically added to a Reactor
 * to interact with the blades of that Reactor. One of the advantages of an AsyncAgent is
 * to reduce the number of requests that pass between threads. The other can be to perform
 * multiple requests against the other blades.
 * </p>
 * Any needed parameters are not passed to the startAReq, but are instead are to be
 * passed in the constructor of the subclasses of AsyncAgent.
 */
@Deprecated
public interface AsyncAgent<RESPONSE_TYPE> extends Blade {
    abstract AsyncRequest<RESPONSE_TYPE> startAReq();
}
