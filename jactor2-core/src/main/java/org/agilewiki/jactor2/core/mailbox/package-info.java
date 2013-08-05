/**
 * <h1>Non-blocking, Atomic and Thread-bound Mailboxes</h1>
 * <p>
 *     Mailboxes are used by the Event and Request classes to pass messages to actors,
 *     though the Mailbox interface does include a few methods that can be used by
 *     application developers.
 * </p>
 * <p>
 *     Actors can use 3 different classes of mailboxes: NonBlockingMailbox, AtomicMailbox
 *     and ThreadBoundMailbox. Mailbox instances are easily created, though an instance of
 *     JAContext is required. Three additional parameters can be passed to the constructor
 *     to configure a mailbox.
 * </p>
 * <p>
 *     <b>int initialBufferSize</b> This is the initial size of the outbox for each unique
 *     message destination. When not provided, JAConfig.getInitialBufferSize() is used instead.
 * </p>
 * <p>
 *     <b>int initialLocalQueueSize</b> The initial size of the ArrayDeque's used by the mailbox's
 *     inbox. When not provided, JAConfig.getInitialLocalMessageQueueSize() is used instead.
 * </p>
 * <p>
 *     <b>Runnable onIdle</b> The onIdle.run method is called when the mailbox becomes idle.
 *     (This parameter does not apply to ThreadBoundMailbox.)
 * </p>
 */
package org.agilewiki.jactor2.core.mailbox;