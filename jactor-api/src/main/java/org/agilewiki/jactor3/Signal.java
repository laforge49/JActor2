package org.agilewiki.jactor3;

/**
 * A Signal is sent to an Actor without expectation of a Response.
 */
public interface Signal<TARGET extends Actor> extends Message {

    /**
     * Calls a method on the target actor to process the Signal.
     *
     * @param _targetActor The actor that the signal was sent to.
     * @return A message to be sent to another actor, or null.
     */
    Message processSignal(final TARGET _targetActor);
}
