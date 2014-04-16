package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.closeable.Closeable;
import org.agilewiki.jactor2.core.requests.ExceptionHandler;
import org.agilewiki.jactor2.core.requests.RequestImpl;
import org.agilewiki.jactor2.core.requests.SyncRequest;

import java.util.Queue;

/**
 * Base class for internal reactor implementations.
 */
public interface ReactorImpl extends Closeable, Runnable, Blade {

    /**
     * Initialize the ReactorImpl.
     *
     * @param _reactor    The Reactor of this ReactorImpl.
     */
    void initialize(final Reactor _reactor);

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
    NonBlockingReactor getParentReactor();

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

    boolean isClosing();

    String getReasonForFailure();

    /**
     * Close the reactor;
     *
     * @param _reason    The reason why the reactor is being closed,
     *                   or null if not a failure.
     */
    void fail(final String _reason) throws Exception;

    /**
     * Returns the message currently being processed.
     *
     * @return The message currently being processed, or null.
     */
    RequestImpl getCurrentRequest();

    /**
     * Assigns the message currently being processed.
     *
     * @param _message The message currently being processed.
     */
    void setCurrentRequest(final RequestImpl _message);

    /**
     * Returns true when there is a message in the inbox that can be processed.
     * (This method is not thread safe and must be called on the targetReactor's thread.)
     *
     * @return True if there is a message in the inbox that can be processed.
     */
    boolean hasWork();

    /**
     * Returns true when a message has been passed from another thread.
     *
     * @return True when a message has been passed from another thread.
     */
    boolean hasConcurrent();

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
    ExceptionHandler setExceptionHandler(
            final ExceptionHandler _handler);

    /**
     * Returns the current exception handler.
     *
     * @return The current exception handler, or null.
     */
    ExceptionHandler getExceptionHandler();

    /**
     * Add a message directly to the input queue of a Reactor.
     *
     * @param _message A message.
     * @param _local   True when the current thread is assigned to the targetReactor.
     */
    void unbufferedAddMessage(final RequestImpl _message,
                                     final boolean _local);

    /**
     * Adds messages directly to the queue.
     *
     * @param _messages Previously buffered messages.
     */
    void unbufferedAddMessages(final Queue<RequestImpl> _messages)
            throws Exception;

    /**
     * Buffers a message in the sending targetReactor for sending later.
     *
     * @param _message Message to be buffered.
     * @param _target  The reactor that should eventually receive this message
     * @return True if the message was buffered.
     */
    boolean buffer(final RequestImpl _message, final ReactorImpl _target);

    /**
     * Signals the start of a request.
     */
    void requestBegin(final RequestImpl _requestImpl);

    /**
     * Signals that a request has completed.
     *
     * @param _message The request that has completed
     */
    void requestEnd(final RequestImpl _message);

    /**
     * Returns true when there is code to be executed when the inbox is emptied.
     *
     * @return True when there is code to be executed when the inbox is emptied.
     */
    boolean isIdler();

    /**
     * A noop request used for synchronizing state.
     *
     * @return null.
     */
    SyncRequest<Void> nullSReq();

    /**
     * Check if the current message has timed out and poll any child reactors for same.
     */
    void reactorPoll() throws Exception;

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

    boolean isSlow();

    boolean isCommonReactor();

    /**
     * The time when processing began on the current message.
     */
    long getMessageStartTimeMillis();

    void setMessageStartTimeMillis(long messageStartTimeMillis);

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
     * @param t the exception (throwable) to log
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
     * @param t the exception (throwable) to log
     */
    void error(String msg, Throwable t);
}
