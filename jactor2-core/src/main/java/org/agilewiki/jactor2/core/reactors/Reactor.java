package org.agilewiki.jactor2.core.reactors;

import org.agilewiki.jactor2.core.blades.Blade;
import org.agilewiki.jactor2.core.blades.ExceptionHandler;
import org.agilewiki.jactor2.core.facilities.Facility;
import org.agilewiki.jactor2.core.messages.SyncRequest;
import org.agilewiki.jactor2.core.plant.Plant;
import org.agilewiki.jactor2.core.util.Closeable;
import org.agilewiki.jactor2.core.util.Closer;

/**
 * The Reactor interface identifies the processing methods that can be used by applications.
 */
public interface Reactor extends AutoCloseable, Closer, Blade {

    ReactorImpl getReactorImpl();

    Plant getPlant();

    /**
     * Returns the facility.
     *
     * @return The facility.
     */
    Facility getFacility();

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

    /**
     * Returns true if close() has been called already.
     * Can be called from anywhere.
     *
     * @return true if close() has already been called.
     */
    boolean isClosing();

    SyncRequest<Void> nullSReq();
}
