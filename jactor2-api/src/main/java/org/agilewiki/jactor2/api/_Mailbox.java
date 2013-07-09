package org.agilewiki.jactor2.api;

/**
 * _Mailbox defines the internal API used by RequestBase and UnboundRequestBase
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
     * @param _targetActor For Request's (bound requests), _targetActor is null.
     *                     For UnboundRequest's, _targetActor is the actor
     *                     to which the request is applied.
     * @param <A>          The target actor type.
     */
    <A extends Actor> void signal(final _Request<Void, A> _request,
                                  final A _targetActor) throws Exception;

    /**
     * A _Request object is enqueued by this mailbox for subsequent processing.
     * The request is buffered by _sourceMailbox until the source mailbox has no more
     * requests or responses to process.
     * No result is returned.
     * Any uncaught exceptions which occur while processing the request
     * are logged as a warning.
     *
     * @param _request       Defines the operation to be applied to the target actor.
     * @param _sourceMailbox The originating mailbox where the request is buffered.
     * @param _targetActor   For Request's (bound requests), _targetActor is null.
     *                       For UnboundRequest's, _targetActor is the actor
     *                       to which the request is applied.
     * @param <A>            The target actor type.
     */
    <A extends Actor> void signal(final _Request<Void, A> _request,
                                  final Mailbox _sourceMailbox, final A _targetActor)
            throws Exception;

    /**
     * _Request and ResponseProcessor objects are enqueued by this mailbox
     * for subsequent processing.
     * The request is buffered by _sourceMailbox until the source mailbox has no more
     * requests or responses to process.
     * <p>
     * If no exception occurs while processing the request, the ResponseProcessor object and
     * a result object created when the request is processed are enqueued by the source mailbox
     * for subsequent processing.
     * Otherwise the exception is enqueued by the source mailbox in place of the result.
     * </p>
     * <p>
     * The ResponseProcessor and the result/exception are however not enqueued
     * immediately. Rather, they are buffered by this mailbox until there are no more
     * requests or results to process.
     * </p>
     *
     * @param _request       Defines the operation to be applied to the target actor.
     * @param _sourceMailbox The originating mailbox where the request is buffered.
     * @param _targetActor   For Request's (bound requests), _targetActor is null.
     *                       For UnboundRequest's, _targetActor is the actor
     *                       to which the request is applied.
     * @param _rp            The callback used to receive the result of the request.
     * @param <E>            The result type.
     * @param <A>            The target actor type.
     */
    <E, A extends Actor> void send(final _Request<E, A> _request,
                                   final Mailbox _sourceMailbox, final A _targetActor,
                                   final ResponseProcessor<E> _rp) throws Exception;

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
     * @param _targetActor For Request's (bound requests), _targetActor is null.
     *                     For UnboundRequest's, _targetActor is the actor
     *                     to which the request is applied.
     * @param <E>          The result type.
     * @param <A>          The target actor type.
     * @return The result.
     */
    <E, A extends Actor> E call(final _Request<E, A> _request,
                                final A _targetActor) throws Exception;
}
