package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.SOp;
import org.agilewiki.jactor2.core.plant.impl.MetricsTimer;
import org.agilewiki.jactor2.core.reactors.closeable.Closeable;
import org.agilewiki.jactor2.core.reactors.impl.ReactorImpl;

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
     * @param _reason The reason why the reactor is being closed,
     *                or null if not a failure.
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
    SOp<Void> nullSOp();

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
     * @param _closeable The Closeable to be registered.
     * @return True if the Closeable was registered.
     */
    boolean addCloseable(final Closeable _closeable);

    /**
     * Unregister a Closeable.
     *
     * @param _closeable The Closeable to be unregistered.
     * @return True if the Closeable was unregistered.
     */
    boolean removeCloseable(final Closeable _closeable);

    /**
     * Log a message at the WARN level.
     *
     * @param msg the message string to be logged
     */
    void warn(String msg);

    /**
     * Log an exception (throwable) at the WARN level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    void warn(String msg, Throwable t);

    /**
     * Log a message at the ERROR level.
     *
     * @param msg the message string to be logged
     */
    void error(String msg);

    /**
     * Log an exception (throwable) at the ERROR level with an
     * accompanying message.
     *
     * @param msg the message accompanying the exception
     * @param t   the exception (throwable) to log
     */
    void error(String msg, Throwable t);

    /**
     * Reactors are added as resources only on isolation reactors.
     *
     * @param _reactor The resource.
     */
    void addResource(Reactor _reactor);

    /**
     * Returns true except on and for isolation reactors.
     *
     * @param _reactor The resource.
     * @return Generally true.
     */
    boolean isResource(Reactor _reactor);

    /**
     * Returns the MetricsTimer used to track the performance of this Request instance.
     *
     * @param _name The name of the timer.
     * @return the MetricsTimer used to track the performance of this Request instance.
     */
    MetricsTimer getMetricsTimer(final String _name);
}
