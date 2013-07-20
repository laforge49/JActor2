package org.agilewiki.jactor2.api;

/**
 * <p>
 * Message encapsulates the user/application Request which are queued in the Actor's mailbox. The lightweight
 * thread associated with the Actor's mailbox will process the Message asynchronously. Considering the
 * scenario where multiple Actors are required to do the processing e.g PActor1 ---> PActor2 ---> PActor3.
 * The initial Request is passed to the PActor1 which might further pass the ResponseProcessor to PActor2
 * and PActor2 further to PActor3. The send method of the MailBox is used to pass ResponseProcessor across
 * PActor chain as considered in the scenario.
 * </p>
 */

abstract public class Message implements AutoCloseable {

    /**
     * Returns true when the response is to be sent to another mailbox factory.
     *
     * @return True when the response is to be sent to another mailbox factory.
     */
    abstract public boolean isForeign();

    /**
     * @return the responsePending
     */
    abstract public boolean isResponsePending();

    abstract public void eval(final Mailbox _targetMailbox);

    abstract protected void processThrowable(final Mailbox _activeMailbox, final Throwable _t);
}
