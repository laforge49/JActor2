package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.closeable.Closeable;
import org.agilewiki.jactor2.core.impl.reactorsImpl.ReactorImpl;
import org.agilewiki.jactor2.core.plant.Recovery;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * <p>
 * A reactor is a light-weight thread dedicated to processing requests and responses (requests with a response value).
 * </p>
 * <p>
 * A reactor has an input queue of requests/responses not yet processed and
 * a table of requests/responses to be sent to other reactors.
 * </p>
 */
public interface Reactor extends Closeable, Blade {
    /**
     * Returns the object used to implement the reactor.
     *
     * @return The object used to implement the reactor.
     */
    ReactorImpl asReactorImpl();

    /**
     * As a blade, the reactor of a reactor is itself.
     *
     * @return this
     */
    @Override
    Reactor getReactor();

    /**
     * When a reactor is closed, the following occurs:
     * <ol>
     * <li>
     * This reactor is removed from the closeables list of the reactors it depends on.
     * </li>
     * <li>
     * All requests sent by this reactor which are still outstanding are canceled.
     * </li>
     * <li>
     * All the closables in this reactor's closeables list are closed.
     * </li>
     * <li>
     * All the requests/responses that have been sent but which have not yet been disbursed
     * to their destinations are distributed immediately.
     * </li>
     * <li>
     * All incoming requests/responses that have not yet been processed are closed.
     * </li>
     * <li>
     * If there is an active current request, it is interrupted. If it does not
     * complete or throw an exception then hung thread recovery is initiated.
     * </li>
     * </ol>
     */
    @Override
    void close() throws Exception;

    /**
     * Close the reactor;
     *
     * @param _reason    The reason why the reactor is being closed,
     *                   or null if not a failure.
     */
    void fail(final String _reason) throws Exception;

    /**
     * Returns true when there are no more messages in the inbox.
     *
     * @return True when the inbox is empty.
     */
    boolean isInboxEmpty();

    /**
     * Returns a request targeted to the reactor that does nothing.
     * Used for synchronizing state with another reactor.
     *
     * @return A request that does nothing.
     */
    SyncRequest<Void> nullSReq();

    /**
     * Returns the parent reactor. Usually this will be the plant internal reactor.
     * The plant internal reactor has a parent reactor of null.
     *
     * @return The parent reactor or null.
     */
    Reactor getParentReactor();

    /**
     * Register a Closable that will be closed when the reactor closes.
     *
     * @param _closeable    The Closeable to be registered.
     * @return True if the Closeable was registered.
     */
    boolean addCloseable(final Closeable _closeable);

    /**
     * Unregister a Closeable.
     *
     * @param _closeable    The Closeable to be unregistered.
     * @return True if the Closeable was unregistered.
     */
    boolean removeCloseable(final Closeable _closeable);

    /**
     * Returns the Recovery object used by this reactor.
     * The default value is the Recovery object used by the parent reactor when
     * this reactor was created.
     *
     * @return The Recovery object.
     */
    Recovery getRecovery();

    /**
     * Change the Recovery object used by this reactor.
     *
     * @param _recovery    The new Recovery object.
     */
    void setRecovery(final Recovery _recovery);
}
