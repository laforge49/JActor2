package org.agilewiki.jactor2.core.requests;

import org.agilewiki.jactor2.core.requests.impl.RequestImpl;

/**
 * API for a synchronous native request.
 */
public interface SyncNativeRequest<RESPONSE_TYPE>
        extends SyncOperation<RESPONSE_TYPE>, RequestImpl<RESPONSE_TYPE> {
}
