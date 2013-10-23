package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.messages.AsyncRequest;

/**
 * Creates a validate request to validate the changes made by a transaction.
 *
 * @param <CHANGES>
 */
public interface Validator<CHANGES> {
    /**
     * Creates a request to validate the changes made by a transaction.
     * This request returns null if the changes are valid, otherwise
     * an error message is returned.
     *
     * @param _changes    The changes to be validated.
     * @return The validate request.
     */
    AsyncRequest<String> validateAReq(CHANGES _changes);
}
