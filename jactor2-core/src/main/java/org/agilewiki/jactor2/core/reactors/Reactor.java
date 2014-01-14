package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.impl.CloserImpl;
import org.agilewiki.jactor2.core.impl.ReactorImpl;
import org.agilewiki.jactor2.core.requests.SyncRequest;

/**
 * The Reactor interface identifies the processing methods that can be used by applications.
 */
public interface Reactor extends AutoCloseable, Blade {

    ReactorImpl asReactorImpl();

    /**
     * Replace the current ExceptionHandler with another.
     * <p>
     * When an event or request message is processed by a targetReactor, the current
     * exception handler is set to null. When a request is sent by a targetReactor, the
     * current exception handler is saved in the outgoing message and restored when
     * the response message is processed.
     * </p>
     *
     * @param exceptionHandler The exception handler to be used now.
     *                         May be null if the default exception handler is to be used.
     * @return The exception handler that was previously in effect, or null if the
     *         default exception handler was in effect.
     */
    ExceptionHandler setExceptionHandler(final ExceptionHandler exceptionHandler);

    /**
     * Returns true when there are no more messages in the inbox. This method is generally
     * only called by a processing's onIdle task to determine when to return so that an
     * incoming message can be processed.
     *
     * @return True when the inbox is empty.
     */
    boolean isInboxEmpty();

    SyncRequest<Void> nullSReq();

    Reactor getParentReactor();

    boolean addCloseable(final CloseableBase _closeable) throws Exception;
    boolean removeCloseable(final CloseableBase _closeable);
}
