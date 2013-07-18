package org.agilewiki.jactor2.api;

/**
 * _Mailbox defines the internal API used by RequestBase and EventBase
 * to pass _Request's to a target mailbox.
 */
interface _Mailbox extends MessageSource {

    /**
     * The request is buffered by this mailbox until it has no more
     * requests or responses to process, or flush is called.
     * _Request and ResponseProcessor objects are subsequently enqueued by _targetMailbox
     * for subsequent processing.
     * <p>
     * If no exception occurs while processing the request, the ResponseProcessor object and
     * a result object created when the request is processed are enqueued by this mailbox
     * for subsequent processing.
     * Otherwise the exception is enqueued by this mailbox in place of the result.
     * </p>
     *
     * @param _request           Defines the operation to be applied to the target actor.
     * @param _targetMailbox     The target mailbox where the request is to be sent.
     * @param _targetActor       For Request's (bound requests), _targetActor is null.
     *                           For Event's, _targetActor is the actor
     *                           to which the request is applied.
     * @param _responseProcessor The callback used to receive the result of the request.
     * @param <E>                The result type.
     * @param <A>                The target actor type.
     */

    <E, A extends Actor> void sendTo(final _Request<E, A> _request,
                                     final Mailbox _targetMailbox,
                                     final A _targetActor,
                                     final ResponseProcessor<E> _responseProcessor)
            throws Exception;

    /**
     * Add a message directly to the queue.
     *
     * @param message A message.
     * @param local   True when the current thread is bound to the mailbox.
     */
    void unbufferedAddMessages(final Message message, final boolean local)
            throws Exception;

    /**
     * Returns true, if the message was buffered for sending later.
     *
     * @param message Message to send-buffer
     * @param target  The mailbox that should eventually receive this message
     * @return true, if buffered
     */
    boolean buffer(final Message message, final Mailbox target);

    /**
     * Returns true, if this mailbox is currently processing messages.
     */
    boolean isRunning();
}
