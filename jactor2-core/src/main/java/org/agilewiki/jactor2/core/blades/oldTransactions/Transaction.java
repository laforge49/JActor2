package org.agilewiki.jactor2.core.blades.oldTransactions;

import org.agilewiki.jactor2.core.messages.AsyncRequest;

/**
 * A transaction.
 *
 * @param <STATE_WRAPPER> The type of state wrapper.
 */
public interface Transaction<STATE_WRAPPER> {

    /**
     * Creates a request to update some state.
     *
     * @param _stateWrapper Tracks state changes without altering the actual state.
     * @return The update request.
     */
    AsyncRequest<Void> updateAReq(STATE_WRAPPER _stateWrapper);
}
