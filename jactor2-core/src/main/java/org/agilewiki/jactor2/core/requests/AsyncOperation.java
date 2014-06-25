package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.reactors.CommonReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

/**
 * An asynchronous operation.
 */
public interface AsyncOperation<RESPONSE_TYPE> extends Operation<RESPONSE_TYPE> {
}
