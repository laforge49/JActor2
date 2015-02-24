package org.agilewiki.jactor2.core.messages.alt;

import org.agilewiki.jactor2.core.messages.SyncOperation;
import org.agilewiki.jactor2.core.messages.impl.RequestImpl;

/**
 * API for a synchronous native request.
 */
public interface SyncNativeRequest<RESPONSE_TYPE>
        extends SyncOperation<RESPONSE_TYPE>, RequestImpl<RESPONSE_TYPE> {
}
