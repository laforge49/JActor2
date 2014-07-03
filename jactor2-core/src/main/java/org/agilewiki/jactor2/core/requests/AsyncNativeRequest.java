package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.requests.impl.AsyncRequestImpl;

/**
 * API for a native asynchronous request.
 */
public interface AsyncNativeRequest<RESPONSE_TYPE>
        extends AsyncOperation<RESPONSE_TYPE>, AsyncRequestImpl<RESPONSE_TYPE> {
}
