/**
 * <h1>Non-blocking, Atomic and Thread-bound Mailboxes</h1>
 * <p>
 *     Mailboxes are used by the Event and Request classes to pass messages to actors,
 *     though the MessageProcessor interface does include a few methods that can be used by
 *     application developers.
 * </p>
 * <h2>Configuring a MessageProcessor</h2>
 * <p>
 *     Actors can use 3 different classes of mailboxes: NonBlockingMessageProcessor, AtomicMessageProcessor
 *     and ThreadBoundMessageProcessor. MessageProcessor instances are easily created, though an instance of
 *     JAContext is required. Various parameters can be passed to the constructor
 *     to configure a processing.
 * </p>
 * <p>
 *     <b>int initialBufferSize</b> This is the initial size of the outbox for each unique
 *     message destination. When not provided, JAConfig.getInitialBufferSize() is used instead.
 * </p>
 * <p>
 *     <b>int initialLocalQueueSize</b> The initial size of the ArrayDeque's used by the processing's
 *     inbox. When not provided, JAConfig.getInitialLocalMessageQueueSize() is used instead.
 * </p>
 * <p>
 *     <b>Runnable onIdle</b> The onIdle.run method is called when the processing becomes idle.
 *     (This parameter does not apply to ThreadBoundMessageProcessor.)
 * </p>
 * <p>
 *     <b>Runnable messageProcessor</b> The messageProcessor.run method is called when a
 *     thread-bound processing has messages that need processing. As a result of invoking the
 *     run method, the ThreadBoundMessageProcessor.run method needs to be invoked by the thread that
 *     the processing is bound to. (This parameter does not apply to NonBlockingMessageProcessor nor to
 *     AtomicMessageProcessor.)
 * </p>
 */
package org.agilewiki.jactor2.core.processing;