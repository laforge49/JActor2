package org.agilewiki.pamailbox;

import org.agilewiki.pactor.Actor;
import org.agilewiki.pactor.ResponseProcessor;
import org.agilewiki.pactor._Request;

public interface MessageSource {

    /**
     * Process an incoming response.
     */
    void incomingResponse(final Message message, final PAMailbox responseSource);

    /**
     * Returns true, if the message could be buffered before sending.
     *
     * @param message Message to send-buffer
     * @param target  The MessageSource that should eventually receive this message
     * @return true, if buffered
     */
    boolean buffer(final Message message, final PAMailbox target);

    /**
     * Returns true, if this message source is currently processing messages.
     */
    boolean isRunning();

    <E, A extends Actor> Message createMessage(final boolean _foreign,
            final MessageQueue inbox, final _Request<E, A> request,
            final A targetActor, final ResponseProcessor<E> responseProcessor);
}
