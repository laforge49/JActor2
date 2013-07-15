package org.agilewiki.jactor2.api;

/**
 * _Mailbox defines the internal API used by BoundRequestBase and UnboundRequestBase
 * to pass _Request's to a target mailbox.
 */
interface _Mailbox {

    /**
     * A _Request object is enqueued by this mailbox for subsequent processing.
     * The request is not buffered.
     * No result is returned.
     * Any uncaught exceptions which occur while processing the request
     * are logged as a warning.
     *
     * @param _request     Defines the operation to be applied to the target actor.
     * @param _targetActor For BoundRequest's (bound requests), _targetActor is null.
     *                     For UnboundRequest's, _targetActor is the actor
     *                     to which the request is applied.
     * @param <A>          The target actor type.
     */
    <A extends Actor> void signal(final _Request<Void, A> _request,
                                  final A _targetActor) throws Exception;

    /**
     * The request is buffered by this mailbox until it has no more
     * requests or responses to process.
     * The _Request is then enqueued on the target mailbox for subsequent processing.
     * No result is returned.
     * Any uncaught exceptions which occur while processing the request
     * are logged as a warning.
     *
     * @param _request       Defines the operation to be applied to the target actor.
     * @param _targetMailbox The target mailbox where the signal is to be processed.
     * @param _targetActor   For BoundRequest's (bound requests), _targetActor is null.
     *                       For UnboundRequest's, _targetActor is the actor
     *                       to which the request is applied.
     * @param <A>            The target actor type.
     */
    <A extends Actor> void signalTo(final _Request<Void, A> _request,
                                    final Mailbox _targetMailbox,
                                    final A _targetActor)
            throws Exception;

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
     * @param _targetActor       For BoundRequest's (bound requests), _targetActor is null.
     *                           For UnboundRequest's, _targetActor is the actor
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
     * A _Request object is enqueued by this mailbox for subsequent processing and
     * the current thread is blocked until there is a result from processing the request.
     * The request is not buffered.
     * <p>
     * If no exception occurs while processing the request,
     * a result object created when the request is processed is returned on the caller's thread.
     * Otherwise the exception is thrown on the caller's thread.
     * </p>
     * <p>
     * The result/exception are however not returned/thrown
     * immediately. Rather, they are buffered by this mailbox until there are no more
     * requests or results to process.
     * </p>
     *
     * @param _request     Defines the operation to be applied to the target actor.
     * @param _targetActor For BoundRequest's (bound requests), _targetActor is null.
     *                     For UnboundRequest's, _targetActor is the actor
     *                     to which the request is applied.
     * @param <E>          The result type.
     * @param <A>          The target actor type.
     * @return The result.
     */
    <E, A extends Actor> E call(final _Request<E, A> _request,
                                final A _targetActor) throws Exception;
}
