package org.agilewiki.jactor2.core.blades.transactions;

import org.agilewiki.jactor2.core.messages.AsyncRequest;

/**
 * Creates a changeNotification request for notifying the subscriber of the changes made by a transaction.
 *
 * @param <CHANGES>
 */
public interface ChangeNotificationSubscriber<CHANGES> {
    /**
     * Creates a request to notifiy the subscriber of the changes made by a transaction.
     *
     * @param _changes    The changes that have been made.
     * @return The changeNotification request.
     */
    AsyncRequest<Void> changeNotificationAReq(CHANGES _changes);
}
