
package org.agilewiki.pactor;

/**
 * <p>
 * A mailbox is a container for holding the incoming messages that are send to a Actor( Called here POJO Actor). Every
 * PActor has associated mailbox, the messages send to the PActor are processed by mailbox. The mailbox implementation 
 * is a lightweight thread which gets activated(if not running) when the messages are added to the mailbox. The mailbox 
 * thread keeps running till all the messages in the mailbox are processed.
 * </p><p>
 * Request are submitted to the MailboxFactory which internally calls the mailbox thread to consume the Request.
 * </p>
 */

public interface Mailbox {
	
    /**
     * Creates the mailbox.
     *
     */
    Mailbox createMailbox();

    /**
     * Adds the resources which are required to be closed(released) when the mailbox 
     * is shutdown.
     *
     * @param closable resource whose cleanup should be done in when the mailbox is 
     * shutdown.
     */
    void addAutoClosable(final AutoCloseable closeable);

    /**
     * The shutdown should make sure that the a lightweight threads required to process 
     * the messages associated with this mailbox is terminated. This will result in the 
     * graceful shutdown of the threads. The messages send to the mailbox after calling 
     * the shutdown would not be executed.
     *
     */    
    void shutdown();

    /**
     * This should send the Request to the associated mailbox's queue in asynchronous
     * mode.
     *
     * @param request Request Object that should encapsulate the Requested Information
     * to be processed.
     */        
    void send(final Request<?> request) throws Exception;

    /**
     * This should send the Request to the associated mailbox's queue with specific return 
     * type which is encapsulated in ResponseProcessor. reply with VoidResponseProcessor
     * will act same as the send method.
     * 
     *
     * @param request Request Object that should encapsulate the Requested Information
     * to be processed.
     * @param source The mailbox reference where the Response Message should be dispatched.
     * @param responseProcessor The response processor implementation.
     */ 
    <E> void reply(final Request<E> request, final Mailbox source,
            final ResponseProcessor<E> responseProcessor)
            throws Exception;

    /**
     * This should send the Request to the associated mailbox's queue in synchronous mode. 
     * The thread that invokes this operation will wait for process to be executed and 
     * response to be send back the invoking thread.
     * 
     * @param request Request Object that should encapsulate the Requested Information
     * to be processed.
     */
    <E> E pend(final Request<E> request) throws Exception;

    ExceptionHandler setExceptionHandler(final ExceptionHandler exceptionHandler);
}
