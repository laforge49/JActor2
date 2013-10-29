package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.AsyncRequest;

/**
 * Creates a validate request to validate the changes made by a transaction.
 *
 * @param <IMMUTABLE_CHANGES>
 */
public interface Validator<IMMUTABLE_CHANGES> extends Blade {
    String getPrefix();

    /**
     * Creates a request to validate the changes made by a transaction.
     * This request throws an exception if the change is not valid.
     *
     * @param _changes The changes to be validated.
     * @return The validate request.
     */
    AsyncRequest<Void> validateAReq(IMMUTABLE_CHANGES _changes);
}
