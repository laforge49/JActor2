package org.agilewiki.pactor;

interface _Mailbox {

    /**
     * This should signal the _Request to the associated mailbox's queue in asynchronous
     * mode.
     *
     * @param request _Request Object that should encapsulate the Requested Information
     *                to be processed.
     */
    <A extends Actor> void signal(final _Request<Void, A> request, final A targetActor) throws Exception;

    /**
     * Same as signal(_Request) until buffered message are implemented.
     */
    <A extends Actor> void signal(final _Request<Void, A> request,
                                  final Mailbox source,
                                  final A targetActor) throws Exception;

    /**
     * This should signal the _Request to the associated mailbox's queue with specific return
     * type which is encapsulated in ResponseProcessor. send with VoidResponseProcessor
     * will act same as the signal method.
     *
     * @param request           _Request Object that should encapsulate the Requested Information
     *                          to be processed.
     * @param source            The mailbox reference where the Response Message should be dispatched.
     * @param responseProcessor The response processor implementation.
     */
    <E, A extends Actor> void send(final _Request<E, A> request,
                                   final Mailbox source,
                                   final A targetActor,
                                   final ResponseProcessor<E> responseProcessor) throws Exception;

    /**
     * This should signal the _Request to the associated mailbox's queue in synchronous mode.
     * The thread that invokes this operation will wait for process to be executed and
     * response to be signal back the invoking thread.
     *
     * @param request _Request Object that should encapsulate the Requested Information
     *                to be processed.
     */
    <E, A extends Actor> E call(final _Request<E, A> request, final A targetActor) throws Exception;
}
