/**
 * <h1>Non-blocking, Isolation, Thread-bound and Swing-bound Reactors</h1>
 * <p>
 *     While MessageProcessors are used by the Event and AsyncRequest classes to pass and process messages,
 *     the Reactor interface itself include only a few methods for use by
 *     application developers.
 * </p>
 * <h2>Creating a Reactor</h2>
 * <p>
 *     Blades can use 4 different classes of reactors: NonBlockingReactor, IsolationReactor,
 *     ThreadBoundReactor and SwingBoundReactor.
 *     Reactor instances are easily created, with an instance of
 *     Facility as a required parameter. Additional parameters can be also be passed to the constructor
 *     for configuring a reactor:
 * </p>
 * <ul>
 * <li>
 *     <b>int initialBufferSize</b> This is the initial size of the the send buffers used by the outbox.
 *     When not provided, Facility.getInitialBufferSize() is used instead.
 * </li>
 * <li>
 *     <b>int initialLocalQueueSize</b> The initial size of the local queue(s) used by the
 *     inbox. When not provided, Facility.getInitialLocalMessageQueueSize() is used instead.
 * </li>
 * <li>
 *     <b>Runnable onIdle</b> The onIdle.run method is called when the inbox becomes empty.
 *     (This parameter does not apply to ThreadBoundReactor nor to SwingBoundReactor.)
 * </li>
 * <li>
 *     <b>Runnable boundProcessor</b> The boundProcessor.run method is called when a
 *     thread-bound processor has messages that need processing. As a result of invoking the
 *     boundProcessor.run method, the ThreadBoundReactor.run method be invoked in turn by the thread that
 *     the processing is bound to. (This parameter applies only to ThreadBoundReactor.)
 * </li>
 * </ul>
 */
package org.agilewiki.jactor2.core.reactors;