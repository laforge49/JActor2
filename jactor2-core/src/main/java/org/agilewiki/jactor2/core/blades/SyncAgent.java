package org.agilewiki.jactor2.core.blades;

import org.agilewiki.jactor2.core.messages.SyncRequest;

/**
 * A SyncAgent is a blade with a StartSReq and which is dynamically added to a Reactor
 * to interact with the blades of that Reactor. One of the advantages of an SyncAgent is
 * to reduce the number of requests that pass between threads. The other can be to perform
 * multiple local requests against the other blades in that targetReactor in isolation from other requests.
 * </p>
 * Any needed parameters are not passed to the startSReq, but are instead are to be
 * passed in the constructor of the subclasses of SyncAgent.
 */
public interface SyncAgent<RESPONSE_TYPE> extends Blade {
    abstract SyncRequest<RESPONSE_TYPE> startSReq();
}
