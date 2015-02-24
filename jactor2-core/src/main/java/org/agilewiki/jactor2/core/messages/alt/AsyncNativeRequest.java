package org.agilewiki.jactor2.core.messages.alt;

import org.agilewiki.jactor2.core.messages.AsyncOperation;
import org.agilewiki.jactor2.core.messages.impl.AsyncRequestImpl;

/**
 * API for a native asynchronous request.
 */
public interface AsyncNativeRequest<RESPONSE_TYPE>
        extends AsyncOperation<RESPONSE_TYPE>, AsyncRequestImpl<RESPONSE_TYPE> {
}
