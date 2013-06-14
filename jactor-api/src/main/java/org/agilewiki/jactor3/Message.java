package org.agilewiki.jactor3;

/**
 * A message is sent to another Actor and can be a Request or a Signal.
 */
public interface Message<TARGET extends Actor> extends Runnable {

    /**
     * Returns the active exception handler, if any.
     *
     * @return The active exception handler, or null.
     */
    ExceptionHandler getExceptionHandler();

    /**
     * Assigns an exception handler.
     *
     * @param _exceptionHandler    The new exception handler, or null.
     */
    void setExceptionHandler(final ExceptionHandler _exceptionHandler);

    /**
     * Execute the Message on another thread.
     */
    void execute();

    /**
     * Returns the target actor.
     *
     * @return The target actor.
     */
    TARGET getTargetActor();

    /**
     * Process a single message.
     *
     * @return The next message to process, or null.
     */
    Message iteration();
}
