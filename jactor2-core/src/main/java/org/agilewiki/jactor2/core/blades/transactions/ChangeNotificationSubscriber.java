package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.AsyncRequest;

/**
 * Creates a changeNotification request for notifying the subscriber of the changes made by a transaction.
 *
 * @param <IMMUTABLE_CHANGES>
 */
public interface ChangeNotificationSubscriber<IMMUTABLE_CHANGES> extends Blade {
    /**
     * Creates a request to notifiy the subscriber of the changes made by a transaction.
     *
     * @param _changes The changes that have been made.
     * @return The changeNotification request.
     */
    AsyncRequest<Void> changeNotificationAReq(IMMUTABLE_CHANGES _changes);
}
