/**
 * <h1>Non-blocking, Atomic, Thread-bound and Swing-bound Message Processors</h1>
 * <p>
 *     While MessageProcessors are used by the Event and Request classes to pass messages and process messages,
 *     the MessageProcessor interface itself include only a few methods for use by
 *     application developers.
 * </p>
 * <h2>Creating a MessageProcessor</h2>
 * <p>
 *     Actors can use 4 different classes of message processors: NonBlockingMessageProcessor, AtomicMessageProcessor,
 *     ThreadBoundMessageProcessor and SwingBoundMessageProcessor.
 *     MessageProcessor instances are easily created, with an instance of
 *     JAContext as a required parameter. Additional parameters can be also be passed to the constructor
 *     for configuring a message processor:
 * </p>
 * <ul>
 * <li>
 *     <b>int initialBufferSize</b> This is the initial size of the the send buffers used by the outbox.
 *     When not provided, JAContext.getInitialBufferSize() is used instead.
 * </li>
 * <li>
 *     <b>int initialLocalQueueSize</b> The initial size of the locl queueeque(s) used by the
 *     inbox. When not provided, JAContext.getInitialLocalMessageQueueSize() is used instead.
 * </li>
 * <li>
 *     <b>Runnable onIdle</b> The onIdle.run method is called when the inbox becomes empty.
 *     (This parameter does not apply to ThreadBoundMessageProcessor nor to SwingBoundMessageProcessor.)
 * </li>
 * <li>
 *     <b>Runnable boundProcessor</b> The boundProcessor.run method is called when a
 *     thread-bound processor has messages that need processing. As a result of invoking the
 *     boundProcessor.run method, the ThreadBoundMessageProcessor.run method be invoked in turn by the thread that
 *     the processing is bound to. (This parameter applies only to ThreadBoundMessageProcessor.)
 * </li>
 * </ul>
 */
package org.agilewiki.jactor2.core.processing;