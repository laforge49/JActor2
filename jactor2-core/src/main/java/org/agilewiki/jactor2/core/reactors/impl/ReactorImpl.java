package org.agilewiki.jactor2.core.reactors.impl;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.messages.ExceptionHandler;
import org.agilewiki.jactor2.core.messages.SOp;
import org.agilewiki.jactor2.core.plant.impl.MetricsTimer;
import org.agilewiki.jactor2.core.reactors.IsolationReactor;
import org.agilewiki.jactor2.core.reactors.Reactor;
import org.agilewiki.jactor2.core.reactors.closeable.Closeable;

/**
 * Base class for internal reactor implementations.
 */
public interface ReactorImpl extends Closeable, Runnable, Blade {

    /**
     * Initialize the ReactorImpl.
     *
     * @param _reactor The Reactor of this ReactorImpl.
     */
    void initialize(final Reactor _reactor) throws Exception;

    /**
     * Returns the Reactor of this ReactorImpl.
     *
     * @return The Reactor of this ReactorImpl.
     */
    Reactor asReactor();

    /**
     * Returns the parent reactor.
     *
     * @return The parent reactor, or null.
     */
    IsolationReactor getParentReactor();

    /**
     * Returns the initial size of a send buffer.
     *
     * @return The initial size of a send buffer.
     */
    int getInitialBufferSize();

    /**
     * Returns the initial size of the local queue.
     *
     * @return The initial size of the local queue.
     */
    int getInitialLocalQueueSize();

    /**
     * Returns true, if this ReactorImpl is actively processing messages.
     *
     * @return True, if this ReactorImpl is actively processing messages.
     */
    boolean isRunning();

    /**
     * Close the reactor;
     *
     * @param _reason The reason why the reactor is being closed,
     *                or null if not a failure.
     */
    void fail(final String _reason) throws Exception;

    /**
     * Returns true when the inbox is not empty.
     *
     * @return True when the inbox is not empty.
     */
    boolean isInboxEmpty();

    /**
     * Assign an exception handler.
     *
     * @param _handler The new exception handler, or null.
     * @return The old exception handler, or null.
     */
    ExceptionHandler<?> setExceptionHandler(final ExceptionHandler<?> _handler);

    /**
     * A noop request used for synchronizing state.
     *
     * @return null.
     */
    SOp<Void> nullSOp();

    /**
     * Add a closeable to the list of closeables.
     *
     * @param _closeable A closeable to be closed when this ReactorImpl is closed.
     * @return True when the closeable was added to the list.
     */
    boolean addCloseable(final Closeable _closeable);

    /**
     * Remove a closeable from the list of closeables.
     *
     * @param _closeable The closeable to be removed.
     * @return True when the closeable was removed.
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
     * @param _reactorImpl The resource.
     */
    void addResource(ReactorImpl _reactorImpl);

    /**
     * Returns true except on and for isolation reactors.
     *
     * @param _reactorImpl The resource.
     * @return Generally true.
     */
    boolean isResource(ReactorImpl _reactorImpl);

    /**
     * Returns the MetricsTimer used to track the performance of this Request instance.
     *
     * @param _name The name of the timer.
     * @return the MetricsTimer used to track the performance of this Request instance.
     */
    MetricsTimer getMetricsTimer(final String _name);
}
